package com.recipt.member.infrastructure.properties

import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.*

@ConstructorBinding
@ConfigurationProperties(prefix = "security.jwt")
data class JwtTokenProperties(
    private val validateTime: Int,
    private val key: String,
    private val algorithm: String
) {
    fun getValidateTimeMili(): Long = validateTime * 1000L

    fun getSecretKey(): String = Base64.getEncoder().encodeToString(key.toByteArray())

    fun getSignatureAlgorithm(): SignatureAlgorithm = SignatureAlgorithm.valueOf(algorithm)
}












