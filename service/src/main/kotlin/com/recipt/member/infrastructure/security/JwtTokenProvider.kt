package com.recipt.member.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.recipt.core.constants.TokenClaims
import com.recipt.core.enums.RedisKeyEnum
import com.recipt.core.enums.authentication.TokenType
import com.recipt.member.domain.member.entity.Member
import com.recipt.core.enums.member.MemberRole
import com.recipt.core.exception.authentication.RefreshTokenNotFoundException
import com.recipt.member.infrastructure.properties.JwtTokenProperties
import com.recipt.core.http.ReciptAttributes.MEMBER_INFO
import com.recipt.core.model.MemberInfo
import com.recipt.member.application.authentication.dto.TokenResult
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.*

/**
 * @see https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
 */
@Component
class JwtTokenProvider (
    private val objectMapper: ObjectMapper,
    private val jwtTokenProperties: JwtTokenProperties,
    private val stringRedisTemplate: StringRedisTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun generateToken(member: Member): TokenResult {
        val memberInfo = createMemberInfo(member)

        logger.info("MEMBER_INFO: ${objectMapper.writeValueAsString(memberInfo)}")
        return doGenerateToken(memberInfo)
    }


    fun doGenerateToken(memberInfo: MemberInfo): TokenResult {
        val createDate = Date()
        val accessTokenExpirationDate = Date(createDate.time + jwtTokenProperties.access.getValidateTimeMili())
        val refreshTokenExpirationDate = Date(createDate.time + jwtTokenProperties.refresh.getValidateDayMili())
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


        return TokenResult(
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
        ).also {
            stringRedisTemplate.opsForValue()
                .set(RedisKeyEnum.REFRESH_TOKEN.getKey(it.refreshToken), memberInfoJson)
        }
    }

    fun findAndDelete(refreshToken: String): MemberInfo {
        val memberInfo = stringRedisTemplate.opsForValue()
            .get(RedisKeyEnum.REFRESH_TOKEN.getKey(refreshToken))
            ?: throw RefreshTokenNotFoundException()

        stringRedisTemplate.delete(refreshToken)

        return objectMapper.readValue(memberInfo)
    }

    private fun createMemberInfo(member: Member) = MemberInfo(
        email = member.email,
        no = member.no,
        nickname = member.nickname
    )
}