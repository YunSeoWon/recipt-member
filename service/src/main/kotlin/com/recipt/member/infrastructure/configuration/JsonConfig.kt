package com.recipt.member.infrastructure.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.recipt.core.enums.FormatEnum
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class JsonConfig {

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().apply {
        registerModule(KotlinModule(nullIsSameAsDefault = true))
        registerModule(Hibernate5Module())
        registerModule(JavaTimeModule().apply {
            addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(FormatEnum.DATETIME.formatter))
            addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(FormatEnum.DATETIME.formatter))
        })
        enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
}