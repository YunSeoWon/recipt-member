package com.recipt.member.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.recipt.core.constants.TokenClaims
import com.recipt.core.enums.RedisKeyEnum
import com.recipt.core.enums.authentication.TokenType
import com.recipt.core.enums.member.MemberRole
import com.recipt.core.exception.authentication.RefreshTokenNotFoundException
import com.recipt.core.model.MemberInfo
import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.infrastructure.properties.JwtTokenProperties
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration
import java.util.*

/**
 * @see https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
 */
@Component
class JwtTokenProvider(
    private val objectMapper: ObjectMapper,
    private val jwtTokenProperties: JwtTokenProperties,
    private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun generateToken(member: Member): Mono<TokenResult> {
        val memberInfo = createMemberInfo(member)

        logger.info("MEMBER_INFO: ${objectMapper.writeValueAsString(memberInfo)}")
        return doGenerateToken(memberInfo)
    }


    fun doGenerateToken(memberInfo: MemberInfo): Mono<TokenResult> {
        val createDate = Date()
        val accessTokenExpirationDate = Date(createDate.time + jwtTokenProperties.access.getValidateTimeMili())
        val refreshTokenExpirationDate = Date(createDate.time + jwtTokenProperties.refresh.getValidateDayMili())

        logger.info("accessTokenExpiration: ${accessTokenExpirationDate}")
        logger.info("refreshTokenExpirationDate: ${refreshTokenExpirationDate}")

        val memberInfoJson = objectMapper.writeValueAsString(memberInfo)

        val claims = mutableMapOf(
            TokenClaims.ROLE to listOf(MemberRole.USER.role),
            TokenClaims.MEMBER_INFO to memberInfoJson,
            TokenClaims.TYPE to TokenType.getTokenType(false)
        )

        val refreshClaims = mutableMapOf(
            TokenClaims.ROLE to listOf(MemberRole.USER.role),
            TokenClaims.MEMBER_INFO to memberInfoJson,
            TokenClaims.TYPE to TokenType.getTokenType(false)
        )

        val builder = Jwts.builder()
            .setSubject(memberInfo.email)
            .setIssuedAt(createDate)

        val token = TokenResult(
            accessToken = builder
                .setClaims(claims)
                .setExpiration(accessTokenExpirationDate)
                .signWith(jwtTokenProperties.access.getSignatureAlgorithm(), jwtTokenProperties.access.getSecretKey())
                .compact(),
            refreshToken = builder
                .setClaims(refreshClaims)
                .setExpiration(refreshTokenExpirationDate)
                .signWith(jwtTokenProperties.refresh.getSignatureAlgorithm(), jwtTokenProperties.refresh.getSecretKey())
                .compact()
        )

        return reactiveStringRedisTemplate.opsForValue()
            .set(
                RedisKeyEnum.REFRESH_TOKEN.getKey(token.refreshToken),
                memberInfoJson,
                Duration.ofDays(jwtTokenProperties.refresh.validateTime)
            ).map { token }
    }

    fun findAndDelete(refreshToken: String): Mono<MemberInfo> {
        return reactiveStringRedisTemplate.opsForValue()
            .get(RedisKeyEnum.REFRESH_TOKEN.getKey(refreshToken))
            .switchIfEmpty { Mono.error(RefreshTokenNotFoundException()) }
            .map { objectMapper.readValue<MemberInfo>(it) }
            .zipWith(reactiveStringRedisTemplate.delete(refreshToken))
            .map { it.t1 }
    }

    private fun createMemberInfo(member: Member) = MemberInfo(
        email = member.email,
        no = member.no,
        nickname = member.nickname
    )
}