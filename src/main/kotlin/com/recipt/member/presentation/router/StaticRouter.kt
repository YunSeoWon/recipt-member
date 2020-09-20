package com.recipt.member.presentation.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.RouterFunctions.resources

/**
 * Static 자원에 접근하기 위해 사용하는 Router
 */
@Configuration
class StaticRouter {
    @Bean
    fun staticRoute() = resources("/**", ClassPathResource("static/"));
}