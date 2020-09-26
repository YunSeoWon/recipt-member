package com.recipt.member.application.member

import com.recipt.member.application.member.dto.FollowerProfileSummary
import com.recipt.member.application.member.dto.ProfileSummary
import com.recipt.member.application.member.dto.MyProfile
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.presentation.exception.member.MemberNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class MemberQueryService ( private val memberRepository: MemberRepository ) {

    suspend fun getProfile(memberNo: Int): ProfileSummary {
        return memberRepository.findByIdOrNull(memberNo)?.let {
            ProfileSummary(it)
        }?: throw MemberNotFoundException()
    }

    suspend fun getMyProfile(memberNo: Int): MyProfile {
        return memberRepository.findByIdOrNull(memberNo)?.let {
            MyProfile(it)
        }?: throw MemberNotFoundException()
    }

    // TODO: Paging 적용하기.
    suspend fun getFollowerProfiles(memberNo: Int): List<FollowerProfileSummary> {
        return memberRepository.findFollowerByNo(memberNo).map {
            FollowerProfileSummary(it.nickname, it.profileImageUrl)
        }
    }

    suspend fun checkFollowing(from: Int, to: Int): Boolean {
        return memberRepository.existFollowing(from, to)
    }
}