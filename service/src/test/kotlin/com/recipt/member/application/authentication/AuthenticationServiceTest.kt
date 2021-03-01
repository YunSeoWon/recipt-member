package com.recipt.member.application.authentication

import com.recipt.core.exception.member.MemberNotFoundException
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

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
    private val token = TokenResult("accessToken", "refreshToken")

    @BeforeEach
    fun setUp() {
        authenticationService = AuthenticationService(memberRepository, jwtTokenProvider, passwordEncoder)

        val member = mockk<Member> {
            every { password } returns rightPassword
        }

        every { memberRepository.findByEmail(email) } returns member
        every { memberRepository.findByEmail(not(email)) } returns null

        every { passwordEncoder.matches(rightPassword, rightPassword) } returns true
        every { passwordEncoder.matches(not(rightPassword), rightPassword) } returns false

        every { jwtTokenProvider.generateToken(any<Member>()) } returns Mono.just(token)
    }

    @Test
    fun `토큰 발급`() {
        // when, then
        val rightCommand = TokenCreateCommand(
            email = email,
            password = rightPassword
        )

        val result = authenticationService.getToken(rightCommand)

        StepVerifier.create(result)
            .expectNext(token)
            .expectComplete()
            .verify()

        verify(exactly = 1) {
            memberRepository.findByEmail(email)
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

        val result = authenticationService.getToken(wrongEmailCommand)

        StepVerifier.create(result)
            .expectError(MemberNotFoundException::class.java)
            .verify()

        verify(exactly = 1) {
            memberRepository.findByEmail(notExistedEmail)
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

        val result = authenticationService.getToken(wrongPasswordCommand)

        StepVerifier.create(result)
            .expectError(WrongEmailOrPasswordException::class.java)
            .verify()

        verify(exactly = 1) {
            memberRepository.findByEmail(email)
            passwordEncoder.matches(not(rightPassword), rightPassword)
        }

        verify(exactly = 0) {
            jwtTokenProvider.generateToken(any<Member>())
        }
    }

    @Test
    fun `토큰 재발급`() {
        val refreshToken = "refreshToken"

        every { jwtTokenProvider.findAndDelete(refreshToken) } returns Mono.just(mockk())
        every { jwtTokenProvider.doGenerateToken(any()) } returns Mono.just(token)

        val result = authenticationService.refreshToken(refreshToken)

        StepVerifier.create(result)
            .expectNext(token)
            .verifyComplete()

        verify {
            jwtTokenProvider.findAndDelete(refreshToken)
            jwtTokenProvider.doGenerateToken(any())
        }
    }
}