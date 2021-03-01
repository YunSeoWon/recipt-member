package com.recipt.member.application.member.dto

import com.recipt.member.domain.member.entity.Member

data class MyProfile(
    val email: String,
    val name: String,
    val nickname: String,
    val introduction: String,
    val mobileNo: String,
    val followerCount: Int,
    val totalRecipeReadCount: Int,
    val profileImageUrl: String?
) {
    constructor(member: Member) : this(
        email = member.email,
        name = member.name,
        nickname = member.nickname,
        introduction = member.introduction,
        mobileNo = member.mobileNo,
        followerCount = member.followerCount,
        totalRecipeReadCount = 0,
        profileImageUrl = member.profileImageUrl
    )
}