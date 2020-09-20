package com.recipt.member.presentation.security

import com.recipt.member.presentation.ReciptAttributes.MEMBER_INFO
import com.recipt.member.presentation.ReciptHeaders.AUTH_TOKEN
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.lang.UnsupportedOperationException

/**
 * @see https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
 */
@Component
class SecurityContextRepository(
    private val jwtAuthenticationManager: ReactiveAuthenticationManager
): ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.") // ??
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val request = exchange.request
        val authHeader = request.headers.getFirst(AUTH_TOKEN)

        return authHeader?.let {
            jwtAuthenticationManager.authenticate(UsernamePasswordAuthenticationToken(it, it))
                .map { authentication ->
                    val memberInfo = (authentication as ReciptAuthenticationToken).memberInfo
                    exchange.attributes.put(MEMBER_INFO, memberInfo)

                    SecurityContextImpl(authentication) as SecurityContext
                }
        }?: Mono.empty()
    }
}