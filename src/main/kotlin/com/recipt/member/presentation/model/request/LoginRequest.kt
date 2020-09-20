package com.recipt.member.presentation.model.request

import com.recipt.member.application.authentication.dto.TokenCreateCommand
import org.springframework.security.crypto.password.PasswordEncoder

data class LogInRequest(
    val email: String,
    val password: String
) {
    fun toCommand() = TokenCreateCommand(
        email = email,
        password = password
    )
}