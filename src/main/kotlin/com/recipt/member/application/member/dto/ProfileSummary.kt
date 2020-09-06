package com.recipt.member.application.member.dto

import com.recipt.member.domain.member.entity.Member

data class ProfileSummary (
    val nickname: String,
    val introduction: String,
    val followerCount: Int,
    val totalRecipeReadCount: Int
) {
    constructor(member: Member): this (
        nickname = member.nickname,
        introduction = member.introduction,
        followerCount = member.followerCount,
        totalRecipeReadCount = 0 // TODO: 레시피 모듈 나오면
    )
}