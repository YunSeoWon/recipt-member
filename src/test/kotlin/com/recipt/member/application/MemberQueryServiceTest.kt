package com.recipt.member.application

import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.application.member.dto.FollowerProfileSummary
import com.recipt.member.application.member.dto.MyProfile
import com.recipt.member.application.member.dto.ProfileSummary
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.presentation.exception.member.MemberNotFoundException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
class MemberQueryServiceTest {
    @MockK
    private lateinit var memberRepository: MemberRepository

    private lateinit var memberQueryService: MemberQueryService

    private val _email = "email@email.com"
    private val _nickname = "테스터"
    private val _introduction = "테스터입니다"
    private val _followerCount = 0
    private val _mobileNo = "010-1234-5678"
    private val _profileImageUrl = "imageurl.com"

    @BeforeEach
    fun setUp() {
        memberQueryService = MemberQueryService(memberRepository)
    }

    @Test
    fun `회원 프로필 조회`() {
        val memberNo = 1
        val notExistMemberNo = 2
        val member = mockk<Member> {
            every { nickname } returns _nickname
            every { introduction } returns _introduction
            every { followerCount } returns _followerCount
            every { profileImageUrl } returns _profileImageUrl
        }
        val expected = ProfileSummary(
            nickname = _nickname,
            introduction = _introduction,
            followerCount = _followerCount,
            totalRecipeReadCount = 0,
            profileImageUrl = _profileImageUrl
        )

        every { memberRepository.findByIdOrNull(memberNo) } returns member
        every { memberRepository.findByIdOrNull(not(memberNo)) } returns null

        val result = runBlocking { memberQueryService.getProfile(memberNo) }

        assertThrows<MemberNotFoundException> {
            runBlocking { memberQueryService.getProfile(notExistMemberNo) }
        }

        assertEquals(expected, result)
    }

    @Test
    fun `자신의 프로필 조회`() {
        val memberNo = 1
        val member = mockk<Member> {
            every { email } returns _email
            every { nickname } returns _nickname
            every { introduction } returns _introduction
            every { followerCount } returns _followerCount
            every { mobileNo } returns _mobileNo
            every { profileImageUrl } returns _profileImageUrl
        }
        val expected = MyProfile(
            email = _email,
            nickname = _nickname,
            introduction = _introduction,
            mobileNo = _mobileNo,
            followerCount = _followerCount,
            totalRecipeReadCount = 0,
            profileImageUrl = _profileImageUrl
        )

        every { memberRepository.findByIdOrNull(memberNo) } returns member
        every { memberRepository.findByIdOrNull(not(memberNo)) } returns null

        val result = runBlocking { memberQueryService.getMyProfile(memberNo) }

        assertEquals(expected, result)

        assertThrows<MemberNotFoundException> {
            runBlocking { memberQueryService.getMyProfile(memberNo + 1) }
        }
    }

    @Test
    fun `자신이 팔로우한 회원 리스트 조회하기`() {
        val memberNo = 1
        val nicknames = listOf("팔로워1", "팔로워2")

        val followerList = nicknames.map {
            mockk<Member> {
                every { nickname } returns it
                every { profileImageUrl } returns null
            }
        }

        every { memberRepository.findFollowerByNo(memberNo) } returns followerList

        val result = runBlocking {
            memberQueryService.getFollowerProfiles(memberNo)
        }

        assertEquals(result, nicknames.map { FollowerProfileSummary(it, null) } )
    }
}