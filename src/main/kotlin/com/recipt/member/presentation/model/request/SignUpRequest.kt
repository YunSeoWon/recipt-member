package com.recipt.member.presentation.model.request

import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.presentation.ValidationPatterns
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class SignUpRequest(
    @field:Email
    val email: String,

    @field:NotBlank
    val nickname: String,

    @Pattern(regexp = ValidationPatterns.PASSWORD)
    val password: String,

    @Pattern(regexp = ValidationPatterns.PHONE)
    val mobileNo: String
) {
    fun toCommand(passwordEncoder: PasswordEncoder) = SignUpCommand(
        email = email,
        nickname = nickname,
        mobileNo = mobileNo,
        password = passwordEncoder.encode(password)
    )
}