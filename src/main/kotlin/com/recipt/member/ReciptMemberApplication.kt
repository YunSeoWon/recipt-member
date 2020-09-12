package com.recipt.member

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux


@EnableWebFlux
@SpringBootApplication
class ReciptMemberApplication

fun main(args: Array<String>) {
    runApplication<ReciptMemberApplication>(*args)
}
