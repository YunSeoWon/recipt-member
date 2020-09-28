package com.recipt.member.domain.member.repository

import com.recipt.member.domain.member.entity.Member

interface MemberAggregateRepository {
    fun findFollowerByNo(no: Int): List<Member>
    fun existFollowing(from: Int, to: Int): Boolean
}