package com.recipt.member.application.member

import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.presentation.exception.member.DuplicatedMemberException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class MemberCommandServiceTest {
    @MockK
    private lateinit var memberRepository: MemberRepository

    private lateinit var memberCommandService: MemberCommandService

    @BeforeEach
    fun setUp() {
        memberCommandService = MemberCommandService(memberRepository)
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


}