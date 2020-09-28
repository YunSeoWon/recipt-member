package com.recipt.core.model

data class MemberInfo (
    val email: String,
    val no: Int,
    val nickname: String
) {
    companion object {
        val TEST_MEMBER_INFO = MemberInfo(
            email = "email@email.com",
            no = 0,
            nickname = "nickname"
        )
    }
}