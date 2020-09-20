package com.recipt.member

import com.recipt.member.infrastructure.properties.PropertiesBase
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux


@EnableWebFlux
@ConfigurationPropertiesScan(basePackageClasses = [PropertiesBase::class])
@SpringBootApplication(exclude = [ReactiveUserDetailsServiceAutoConfiguration::class])
class ReciptMemberApplication

fun main(args: Array<String>) {
    runApplication<ReciptMemberApplication>(*args)
}
