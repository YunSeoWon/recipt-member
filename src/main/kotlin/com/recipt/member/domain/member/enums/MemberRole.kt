package com.recipt.member.domain.member.enums

enum class MemberRole {
    ADMIN, USER;

    val role: String = "ROLE_$name"
}