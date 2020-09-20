package com.recipt.member.presentation.handler

import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.presentation.model.request.SignUpRequest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import javax.validation.Validator

@ExtendWith(MockKExtension::class)
internal class MemberHandlerTest {
    @MockK
    private lateinit var memberQueryService: MemberQueryService

    @MockK
    private lateinit var memberCommandService: MemberCommandService

    @MockK
    private lateinit var validator: Validator

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var authenticationService: AuthenticationService

    private lateinit var memberHandler: MemberHandler

    companion object {
        private const val ENCODED = "E@DDF$#@T"
    }

    @BeforeEach
    fun setUp() {
        memberHandler = MemberHandler(
            memberQueryService,
            memberCommandService,
            validator,
            passwordEncoder,
            authenticationService
        )

        every { validator.validate(any<Any>()) } returns emptySet()
        every { passwordEncoder.encode(any()) } returns ENCODED
    }

    @Test
    fun `프로필 단건 조회 테스트`() {
        val memberNo = 1

        val request = MockServerRequest.builder()
            .pathVariable("memberNo", "$memberNo")
            .build()

        coEvery { memberQueryService.getProfile(memberNo) } returns mockk()

        val result = runBlocking { memberHandler.getProfile(request) }

        assertEquals(HttpStatus.OK, result.statusCode())
    }

    @Test
    fun `회원 가입`() {
        val body = SignUpRequest(
            email = "email@email.com",
            password = "password",
            nickname = "nickname",
            mobileNo = "010-1121-1121"
        )

        val request = MockServerRequest.builder()
            .body(Mono.just(body))

        every { memberCommandService.signUp(any()) } just runs

        val result = runBlocking { memberHandler.signUp(request) }

        assertEquals(HttpStatus.CREATED, result.statusCode())
    }
}