package com.recipt.member.domain.member.repository

import com.recipt.member.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Int> {
    fun findTopByEmailOrNickname(email: String, nickName: String): Member?
}