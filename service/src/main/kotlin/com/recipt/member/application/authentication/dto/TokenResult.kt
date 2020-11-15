package com.recipt.member.application.authentication.dto

data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)