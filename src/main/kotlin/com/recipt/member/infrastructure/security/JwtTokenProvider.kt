package com.recipt.member.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.presentation.model.MemberInfo
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.*

/**
 * @see https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
 */
@Component
class JwtTokenProvider (
    private val objectMapper: ObjectMapper
) {
    // TODO: properties로 관리하자.
    companion object {
        private const val tokenValidTime = 30 * 60 * 1000L
        private val SECRET_KEY = Base64.getEncoder().encodeToString("recipt".toByteArray())
        private val algorithm = SignatureAlgorithm.HS256
    }

    fun getAllClaimsFromToken(token: String) = Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(token)
        .body

    fun getUsernameFromToken(token: String) = getAllClaimsFromToken(token).subject

    fun getExpirationDateFromToken(token: String) = getAllClaimsFromToken(token).expiration

    fun isTokenExpired(token: String) = getExpirationDateFromToken(token).before(Date())

    fun generateToken(member: Member): String {
        val memberInfo = MemberInfo(member)
        return doGenerateToken(
            claims = mutableMapOf(
                "role" to listOf("ROLE_USER"),
                "memberInfo" to objectMapper.writeValueAsString(memberInfo)
            ),
            username = member.email
        )
    }


    fun doGenerateToken(claims: MutableMap<String, Any>, username: String): String {
        val createDate = Date()
        val expirationDate = Date(createDate.time + tokenValidTime)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(createDate)
            .setExpiration(expirationDate)
            .signWith(algorithm, SECRET_KEY)
            .compact()
    }

    fun validateToken(token: String) = !isTokenExpired(token)
}