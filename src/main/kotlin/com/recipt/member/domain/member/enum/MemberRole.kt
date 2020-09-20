package com.recipt.member.domain.member.enum

enum class MemberRole {
    ADMIN, USER;

    val role: String = "ROLE_$name"
}