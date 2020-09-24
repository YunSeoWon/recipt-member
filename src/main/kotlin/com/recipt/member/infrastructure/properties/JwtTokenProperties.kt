package com.recipt.member.infrastructure.properties

import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties(prefix = "security.jwt")
data class JwtTokenProperties(
    private val validateTime: Long,
    private val key: String,
    private val algorithm: String
) {
    val validateTimeMili: Long
        get() = validateTime * 1000

    val secretKey: String
        get() = Base64.getEncoder().encodeToString(key.toByteArray())

    val signatureAlgorithm: SignatureAlgorithm
        get() = SignatureAlgorithm.valueOf(algorithm)
}












