package com.recipt.member.infrastructure.configuration

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class ReactiveRedisConfig(
    private val redisProperties: RedisProperties
) {

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(
            RedisStandaloneConfiguration(
                redisProperties.host,
                redisProperties.port
            ).apply {
                password = RedisPassword.of(redisProperties.password)
            }
        )
    }

    @Bean
    fun reactiveStringRedisTemplate(): ReactiveStringRedisTemplate {
        val serializer = StringRedisSerializer()
        
        return RedisSerializationContext.newSerializationContext<String, String>()
            .key(serializer)
            .value(serializer)
            .hashKey(serializer)
            .hashValue(serializer)
            .build()
            .let { ReactiveStringRedisTemplate(reactiveRedisConnectionFactory(), it) }
    }
}