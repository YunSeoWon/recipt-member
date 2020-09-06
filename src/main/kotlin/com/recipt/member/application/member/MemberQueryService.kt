package com.recipt.member.application.member

import com.recipt.member.application.member.dto.ProfileSummary
import com.recipt.member.application.member.dto.MyProfile
import com.recipt.member.domain.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class MemberQueryService ( private val memberRepository: MemberRepository ) {

    suspend fun getProfile(memberNo: Int): ProfileSummary {
        return memberRepository.findByIdOrNull(memberNo)?.let {
            ProfileSummary(it)
        }?: throw Exception()
    }

    suspend fun getMyProfile(memberNo: Int): MyProfile {
        return memberRepository.findByIdOrNull(memberNo)?.let {
            MyProfile(it)
        }?: throw Exception()
    }
}