package com.recipt.member.application.authentication.dto

data class TokenCreateCommand(
    val email: String,
    val password: String
)