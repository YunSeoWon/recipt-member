package com.recipt.core.enums.member

enum class MemberRole {
    ADMIN, USER;

    val role: String = "ROLE_$name"
}