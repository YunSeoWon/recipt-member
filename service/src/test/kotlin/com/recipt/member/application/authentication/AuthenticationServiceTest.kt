package com.recipt.member.application.authentication

import com.recipt.core.exception.member.WrongEmailOrPasswordException
import com.recipt.member.application.authentication.dto.TokenCreateCommand
import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.infrastructure.security.JwtTokenProvider
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockKExtension::class)
internal class AuthenticationServiceTest {

    @MockK
    private lateinit var memberRepository: MemberRepository

    @MockK
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var authenticationService: AuthenticationService

    private val rightPassword = "password"
    private val wrongPassword = "${rightPassword}1"
    private val email = "email@email.com"
    private val notExistedEmail = "a$email"

    @BeforeEach
    fun setUp() {
        authenticationService = AuthenticationService(memberRepository, jwtTokenProvider, passwordEncoder)

        val member = mockk<Member> {
            every { password } returns rightPassword
        }

        every { memberRepository.findFirstByEmail(email) } returns member
        every { memberRepository.findFirstByEmail(not(email)) } returns null

        every { passwordEncoder.matches(rightPassword, rightPassword) } returns true
        every { passwordEncoder.matches(not(rightPassword), rightPassword) } returns false

        every { jwtTokenProvider.generateToken(any<Member>()) } returns TokenResult(
            "accessToken", "refreshToken"
        )
    }

    @Test
    fun `토큰 발급`() {
        // when, then
        val rightCommand = TokenCreateCommand(
            email = email,
            password = rightPassword
        )

        assertDoesNotThrow { authenticationService.getToken(rightCommand) }

        verify(exactly = 1) {
            memberRepository.findFirstByEmail(email)
            passwordEncoder.matches(rightPassword, rightPassword)
            jwtTokenProvider.generateToken(any<Member>())
        }
    }

    @Test
    fun `토큰 발급 실패 (회원이 존재하지 않을때)`() {
        val wrongEmailCommand = TokenCreateCommand(
            email = notExistedEmail,
            password = rightPassword
        )

        assertThrows<WrongEmailOrPasswordException> {
            authenticationService.getToken(wrongEmailCommand)
        }

        verify(exactly = 1) {
            memberRepository.findFirstByEmail(notExistedEmail)
        }

        verify(exactly = 0) {
            passwordEncoder.matches(rightPassword, rightPassword)
            jwtTokenProvider.generateToken(any<Member>())
        }
    }

    @Test
    fun `토큰 발급 실패 (비밀번호가 틀릴 때)`() {
        val wrongPasswordCommand = TokenCreateCommand(
            email = email,
            password = wrongPassword
        )

        assertThrows<WrongEmailOrPasswordException> {
            authenticationService.getToken(wrongPasswordCommand)
        }

        verify(exactly = 1) {
            memberRepository.findFirstByEmail(email)
            passwordEncoder.matches(not(rightPassword), rightPassword)
        }

        verify(exactly = 0) {
            jwtTokenProvider.generateToken(any<Member>())
        }
    }

    @Test
    fun `토큰 재발급`() {
        val refreshToken = "refreshToken"
        val tokenResult = TokenResult(
            "accessToken", "refreshToken"
        )

        every { jwtTokenProvider.findAndDelete(refreshToken) } returns mockk()
        every { jwtTokenProvider.doGenerateToken(any()) } returns tokenResult

        val result = authenticationService.refreshToken(refreshToken)

        verify {
            jwtTokenProvider.findAndDelete(refreshToken)
            jwtTokenProvider.doGenerateToken(any())
        }
    }
}