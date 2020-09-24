package com.recipt.member.application.member.dto

import com.recipt.member.domain.member.entity.Member

data class FollowerProfileSummary(
    val nickname: String,
    val profileImageUrl: String?
) {
    constructor(member: Member): this(member.nickname, member.profileImageUrl)
}