package com.recipt.member.domain.member.repository

import com.recipt.member.domain.member.entity.FollowerMapping
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface FollowerMappingRepository : R2dbcRepository<FollowerMapping, Int> {

    fun findByMemberNoAndFollowerNo(memberNo: Int, followerNo: Int): FollowerMapping?
}