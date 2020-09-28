package com.recipt.member.application.member.dto

data class ProfileModifyCommand(
    val password: String,
    val nickname: String?,
    val mobileNo: String?,
    val introduction: String?,
    val profileImageUrl: String?,
    val newPassword: String?
)