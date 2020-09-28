package com.recipt.member.domain.member.repository

import com.recipt.member.domain.member.entity.FollowerMapping
import org.springframework.data.jpa.repository.JpaRepository

interface FollowerMappingRepository : JpaRepository<FollowerMapping, Int> {

    fun findByMemberNoAndFollowerNo(memberNo: Int, followerNo: Int): FollowerMapping?
}