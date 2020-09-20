package com.recipt.member.domain.member.repository

import com.recipt.member.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Int> {
    fun findByEmailOrNickname(email: String, nickname: String): Member?
    fun findByEmail(email: String): Member?
}