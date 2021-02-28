package com.recipt.member.infrastructure.configuration

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration
import dev.miku.r2dbc.mysql.MySqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

@Configuration
class R2dbcConfig(private val properties: R2dbcProperties) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {

        return MySqlConnectionFactory.from(
            MySqlConnectionConfiguration.builder()
                .host("192.168.0.4")
                .port(3306)
                .database("reciptMember")
                .username(properties.username)
                .password(properties.password)
                .build()
        )
    }

    @Bean
    fun r2dbcEntityTemplate(connectionFactory: ConnectionFactory): R2dbcEntityTemplate {
        return R2dbcEntityTemplate()
    }
}