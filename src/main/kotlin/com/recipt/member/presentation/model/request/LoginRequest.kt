package com.recipt.member.presentation.model.request

import com.recipt.member.application.authentication.dto.TokenCreateCommand
import com.recipt.member.presentation.ValidationPatterns
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.constraints.Pattern

data class LogInRequest(
    val email: String,
    @Pattern(regexp = ValidationPatterns.PASSWORD)
    val password: String
) {
    fun toCommand() = TokenCreateCommand(
        email = email,
        password = password
    )
}