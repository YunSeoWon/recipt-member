package com.recipt.member.presentation.support

import com.recipt.core.enums.member.MemberRole
import com.recipt.core.http.ReciptAttributes
import com.recipt.core.http.ReciptHeaders.AUTH_TOKEN
import com.recipt.core.http.ReciptHeaders.TEST_AUTH_TOKEN
import com.recipt.core.model.MemberInfo
import com.recipt.member.infrastructure.security.ReciptAuthenticationToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.lang.UnsupportedOperationException

@Configuration
@ComponentScan
@EnableWebFluxSecurity
class TestSecurityConfig (
    private val mockSecurityContextRepository: MockSecurityContextRepository,
    private val mockReactiveAuthenticationManger: MockReactiveAuthenticationManger
) {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(mockReactiveAuthenticationManger)
            .securityContextRepository(mockSecurityContextRepository)
            .authorizeExchange()
            .pathMatchers("/members/profiles/me/**", "/members/following/**").hasRole(MemberRole.USER.name)
            .anyExchange().permitAll()
            .and()
            .build()
    }
}

@Component
class MockSecurityContextRepository(
    private val mockReactiveAuthenticationManger: MockReactiveAuthenticationManger
): ServerSecurityContextRepository {
    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.") // ??
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val authHeader = exchange.request.headers.getFirst(AUTH_TOKEN)?.takeIf { it == TEST_AUTH_TOKEN }
            ?: return Mono.empty()

        return authHeader.let {
            mockReactiveAuthenticationManger.authenticate(UsernamePasswordAuthenticationToken(it, it))
                .map { authentication ->
                    val memberInfo = (authentication as ReciptAuthenticationToken).memberInfo
                    exchange.attributes[ReciptAttributes.MEMBER_INFO] = memberInfo

                    SecurityContextImpl(authentication) as SecurityContext
                }
        }
    }

}

@Component
class MockReactiveAuthenticationManger: ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(ReciptAuthenticationToken(MemberInfo.TEST_MEMBER_INFO, listOf(SimpleGrantedAuthority(MemberRole.USER.role))))
    }
}