package com.recipt.member.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer


@Configuration
class WebConfig : WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:8080",
                "http://127.0.0.1:8080"
            )
            .allowedMethods("PUT", "DELETE", "POST", "GET")
            .allowCredentials(true).maxAge(3600)
    }
}