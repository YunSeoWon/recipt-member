package com.recipt.member.infrastructure.properties

import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.*

@ConstructorBinding
@ConfigurationProperties(prefix = "security.jwt")
data class JwtTokenProperties(
    val access: TokenProperties,
    val refresh: TokenProperties
)

data class TokenProperties(
    val validateTime: Long,
    private val key: String,
    private val algorithm: String
) {
    fun getValidateTimeMili(): Long = validateTime * 1000

    fun getValidateDayMili(): Long = validateTime * 1000 * 86400

    fun getSecretKey(): String = Base64.getEncoder().encodeToString(key.toByteArray())

    fun getSignatureAlgorithm(): SignatureAlgorithm = SignatureAlgorithm.valueOf(algorithm)
}









