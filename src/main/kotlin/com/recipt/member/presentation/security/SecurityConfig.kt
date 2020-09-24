package com.recipt.member.presentation.security

import com.recipt.member.domain.member.enums.MemberRole
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val reciptAuthenticationManager: ReactiveAuthenticationManager,
    private val securityContextRepository: SecurityContextRepository
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .exceptionHandling()
            .authenticationEntryPoint { exchange, _ ->
                Mono.fromRunnable { exchange.response.statusCode = HttpStatus.UNAUTHORIZED }
            }
            .accessDeniedHandler { exchange, _ ->
                Mono.fromRunnable { exchange.response.statusCode = HttpStatus.FORBIDDEN }
            }
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(reciptAuthenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers("/members/profiles/me/**", "/members/following/**").hasRole(MemberRole.USER.name)
            .anyExchange().permitAll()
            .and()
            .build()
    }
}