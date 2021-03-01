package com.recipt.core.model

data class MemberInfo(
    val email: String,
    val mobileNo: String,
    val no: Int,
    val name: String,
    val nickname: String
) {
    companion object {
        val TEST_MEMBER_INFO = MemberInfo(
            email = "email@email.com",
            no = 0,
            nickname = "nickname",
            name = "홍길동",
            mobileNo = "01012345678"
        )
    }
}