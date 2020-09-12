package com.recipt.member.presentation.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.RouterFunctions.resources

@Configuration
class StaticRouter {
    @Bean
    fun staticRoute() = resources("/**", ClassPathResource("static/"));
}