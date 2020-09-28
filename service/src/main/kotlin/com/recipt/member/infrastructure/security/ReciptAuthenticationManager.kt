package com.recipt.member.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.recipt.core.http.ReciptAttributes.MEMBER_INFO
import com.recipt.core.model.MemberInfo
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * @see https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
 */
@Component
class ReciptAuthenticationManager(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
): ReactiveAuthenticationManager {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials.toString()

        if (jwtTokenProvider.isTokenExpired(token)) {
            return Mono.empty()
        }

        return try {
            val claims = jwtTokenProvider.getAllClaimsFromToken(token)
            val roles = claims.get("role", List::class.java) as List<String>
            val memberInfo = claims.get(MEMBER_INFO, String::class.java)
                .let { objectMapper.readValue(it, MemberInfo::class.java)}

            val authorities = roles.map { SimpleGrantedAuthority(it) }
            Mono.just(ReciptAuthenticationToken(memberInfo, authorities))
        } catch (e: Exception) {
            logger.error("Error: ", e)
            Mono.empty()
        }
    }
}