package com.recipt.member.application.member.dto

data class SignUpCommand(
    val email: String,
    val password: String,
    val nickname: String,
    val mobileNo: String
)