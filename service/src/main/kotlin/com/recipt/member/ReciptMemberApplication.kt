package com.recipt.member

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@EnableR2dbcRepositories
@ConfigurationPropertiesScan
@SpringBootApplication
class ReciptMemberApplication

fun main(args: Array<String>) {
    runApplication<ReciptMemberApplication>(*args)
}
