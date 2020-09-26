package com.recipt.member.application.member

import com.recipt.member.application.member.dto.ProfileModifyCommand
import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.member.entity.FollowerMapping
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.FollowerMappingRepository
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.presentation.exception.member.DuplicatedMemberException
import com.recipt.member.presentation.exception.member.MemberNotFoundException
import com.recipt.member.presentation.exception.member.WrongPasswordException
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockKExtension::class)
internal class MemberCommandServiceTest {
    @MockK
    private lateinit var memberRepository: MemberRepository

    @MockK
    private lateinit var followerMappingRepository: FollowerMappingRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var memberCommandService: MemberCommandService

    private val testPassword = "password"
    private val newTestPassword = "password1"

    @BeforeEach
    fun setUp() {
        memberCommandService = MemberCommandService(
            memberRepository = memberRepository,
            followerMappingRepository = followerMappingRepository,
            passwordEncoder = passwordEncoder
        )

        /** 귀찮으니까 패스워드 인코더 모킹 여기서.. **/
        every { passwordEncoder.matches(testPassword, testPassword) } returns true
        every { passwordEncoder.matches(not(testPassword), testPassword) } returns false
        every { passwordEncoder.encode(testPassword) } returns testPassword
        every { passwordEncoder.encode(newTestPassword) } returns newTestPassword
    }

    @Test
    fun `회원가입 테스트`() {
        val command = SignUpCommand(
            email = "email@email.com",
            password = "password",
            nickname = "nickname",
            mobileNo = "010-1234-5678"
        )

        every { memberRepository.findByEmailOrNickname(command.email, command.nickname) } returns null
        every { memberRepository.save(any<Member>()) } returns mockk()

        memberCommandService.signUp(command)

        verify(exactly = 1) {
            memberRepository.findByEmailOrNickname(command.email, command.nickname)
            memberRepository.save(any<Member>())
        }
    }

    @Test
    fun `회원가입 테스트(실패, 이메일 중복)`() {
        val command = SignUpCommand(
            email = "email@email.com",
            password = "password",
            nickname = "nickname",
            mobileNo = "010-1234-5678"
        )

        val existedMember = mockk<Member> {
            every { email } returns command.email
        }

        every { memberRepository.findByEmailOrNickname(command.email, command.nickname) } returns existedMember
        every { memberRepository.save(any<Member>()) } returns mockk()

        assertThrows<DuplicatedMemberException> { memberCommandService.signUp(command) }

        verify(exactly = 1) {
            memberRepository.findByEmailOrNickname(command.email, command.nickname)
        }
        verify(exactly = 0) {
            memberRepository.save(any<Member>())
        }
    }

    @Test
    fun `회원가입 테스트(실패, 닉네임 중복)`() {
        val command = SignUpCommand(
            email = "email@email.com",
            password = "password",
            nickname = "nickname",
            mobileNo = "010-1234-5678"
        )

        val existedMember = mockk<Member> {
            every { email } returns "A${command.email}"
            every { nickname } returns command.nickname
        }

        every { memberRepository.findByEmailOrNickname(command.email, command.nickname) } returns existedMember
        every { memberRepository.save(any<Member>()) } returns mockk()

        assertThrows<DuplicatedMemberException> { memberCommandService.signUp(command) }

        verify(exactly = 1) {
            memberRepository.findByEmailOrNickname(command.email, command.nickname)
        }
        verify(exactly = 0) {
            memberRepository.save(any<Member>())
        }
    }

    @Test
    fun `회원 정보 변경`() {
        val memberNo = 1

        val command = mockk<ProfileModifyCommand> {
            every { password } returns testPassword
            every { newPassword } returns newTestPassword
        }
        val member = mockk<Member> {
            every { modify(any(), any()) } just runs
            every { password } returns testPassword
        }

        every { memberRepository.findByIdOrNull(memberNo) } returns member
        every { memberRepository.findByIdOrNull(not(memberNo)) } returns null

        assertDoesNotThrow {
            memberCommandService.modify(memberNo, command)
        }

        verify(exactly = 1) {
            member.modify(any(), any())
        }
    }

    @Test
    fun `회원 정보 변경 (실패, 회원 없음)`() {
        val memberNo = 1
        val notExistMemberNo = 2

        val command = mockk<ProfileModifyCommand> {
            every { password } returns testPassword
            every { newPassword } returns newTestPassword
        }
        val member = mockk<Member> {
            every { modify(any(), any()) } just runs
            every { password } returns testPassword
        }

        every { memberRepository.findByIdOrNull(memberNo) } returns member
        every { memberRepository.findByIdOrNull(not(memberNo)) } returns null

        assertThrows<MemberNotFoundException> {
            memberCommandService.modify(notExistMemberNo, command)
        }

        verify(exactly = 0) {
            member.modify(any(), any())
        }
    }

    @Test
    fun `회원 정보 변경 (실패,  비번 틀림)`() {
        val memberNo = 1

        val wrongPasswordCommand = mockk<ProfileModifyCommand> {
            every { password } returns "wrong$testPassword"
            every { newPassword } returns newTestPassword
        }

        val member = mockk<Member> {
            every { modify(any(), any()) } just runs
            every { password } returns testPassword
        }

        every { memberRepository.findByIdOrNull(memberNo) } returns member
        every { memberRepository.findByIdOrNull(not(memberNo)) } returns null

        assertThrows<WrongPasswordException> {
            memberCommandService.modify(memberNo, wrongPasswordCommand)
        }

        verify(exactly = 0) {
            member.modify(any(), any())
        }
    }

    @Test
    fun `팔로우`() {
        val memberNo = 1
        val followerNo = 2

        every { memberRepository.existsById(followerNo) } returns true
        every { memberRepository.existFollowing(memberNo, followerNo) } returns false
        every { followerMappingRepository.save(any<FollowerMapping>()) } returns mockk()

        assertDoesNotThrow {
            memberCommandService.follow(from = memberNo, to = followerNo)
        }

        verify(exactly = 1) {
            memberRepository.existsById(followerNo)
            memberRepository.existFollowing(memberNo, followerNo)
            followerMappingRepository.save(any<FollowerMapping>())
        }
    }

    @Test
    fun `팔로우 (실패, 팔로우할 회원이 없는 경우)`() {
        val memberNo = 1
        val followerNo = 2

        every { memberRepository.existsById(followerNo) } returns false
        every { memberRepository.existFollowing(memberNo, followerNo) } returns false
        every { followerMappingRepository.save(any<FollowerMapping>()) } returns mockk()

        assertThrows<MemberNotFoundException> {
            memberCommandService.follow(from = memberNo, to = followerNo)
        }

        verify(exactly = 1) {
            memberRepository.existsById(followerNo)
        }
        verify(exactly = 0) {
            memberRepository.existFollowing(memberNo, followerNo)
            followerMappingRepository.save(any<FollowerMapping>())
        }
    }

    @Test
    fun `언팔로우`() {
        val memberNo = 1
        val followerNo = 2

        val followerMapping = FollowerMapping(
            memberNo = memberNo,
            followerNo = followerNo
        )

        every { memberRepository.existsById(followerNo) } returns true
        every {
            followerMappingRepository.findByMemberNoAndFollowerNo(memberNo, followerNo)
        } returns followerMapping
        every { followerMappingRepository.deleteById(any()) } just runs

        assertDoesNotThrow {
            memberCommandService.unfollow(from = memberNo, to = followerNo)
        }

        verify(exactly = 1) {
            memberRepository.existsById(followerNo)
            followerMappingRepository.findByMemberNoAndFollowerNo(memberNo, followerNo)
            followerMappingRepository.deleteById(any())
        }
    }

    @Test
    fun `언팔로우 (실패, 언팔로우할 회원이 없는 경우)`() {
        val memberNo = 1
        val followerNo = 2

        val followerMapping = FollowerMapping(
            memberNo = memberNo,
            followerNo = followerNo
        )

        every { memberRepository.existsById(followerNo) } returns false
        every {
            followerMappingRepository.findByMemberNoAndFollowerNo(memberNo, followerNo)
        } returns followerMapping
        every { followerMappingRepository.deleteById(any()) } just runs

        assertThrows<MemberNotFoundException> {
            memberCommandService.unfollow(from = memberNo, to = followerNo)
        }

        verify(exactly = 1) {
            memberRepository.existsById(followerNo)
        }
        verify(exactly = 0) {
            followerMappingRepository.findByMemberNoAndFollowerNo(memberNo, followerNo)
            followerMappingRepository.deleteById(any())
        }
    }
}