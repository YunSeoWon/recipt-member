package com.recipt.member.application

import com.recipt.member.application.member.MemberQueryService
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

    private val _nickname = "테스터"
    private val _introduction = "테스터입니다"
    private val _followerCount = 0

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
        }
        val expected = ProfileSummary(
            nickname = _nickname,
            introduction = _introduction,
            followerCount = _followerCount,
            totalRecipeReadCount = 0
        )

        every { memberRepository.findByIdOrNull(memberNo) } returns member
        every { memberRepository.findByIdOrNull(notExistMemberNo) } returns null

        val result = runBlocking { memberQueryService.getProfile(memberNo) }

        assertThrows<MemberNotFoundException> {
            runBlocking { memberQueryService.getProfile(notExistMemberNo) }
        }

        assertEquals(expected, result)
    }
}