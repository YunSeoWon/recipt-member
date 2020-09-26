package com.recipt.member.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.enums.MemberRole
import com.recipt.member.infrastructure.properties.JwtTokenProperties
import com.recipt.member.presentation.ReciptAttributes.MEMBER_INFO
import com.recipt.member.presentation.model.MemberInfo
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*

/**
 * @see https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
 */
@Component
class JwtTokenProvider (
    private val objectMapper: ObjectMapper,
    private val jwtTokenProperties: JwtTokenProperties
) {

    fun getAllClaimsFromToken(token: String): Claims = Jwts.parser()
        .setSigningKey(jwtTokenProperties.getSecretKey())
        .parseClaimsJws(token)
        .body

    private fun getExpirationDateFromToken(token: String) = getAllClaimsFromToken(token).expiration

    fun isTokenExpired(token: String) = getExpirationDateFromToken(token).before(Date())

    fun generateToken(member: Member): String {
        val memberInfo = MemberInfo(member)
        return doGenerateToken(
            claims = mutableMapOf(
                "role" to listOf(MemberRole.USER.role),
                MEMBER_INFO to objectMapper.writeValueAsString(memberInfo)
            ),
            username = member.email
        )
    }


    fun doGenerateToken(claims: MutableMap<String, Any>, username: String): String {
        val createDate = Date()
        val expirationDate = Date(createDate.time + jwtTokenProperties.getValidateTimeMili())

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(createDate)
            .setExpiration(expirationDate)
            .signWith(jwtTokenProperties.getSignatureAlgorithm(), jwtTokenProperties.getSecretKey())
            .compact()
    }
}