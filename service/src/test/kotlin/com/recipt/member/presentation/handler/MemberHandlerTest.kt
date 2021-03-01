package com.recipt.member.presentation.handler

import com.recipt.core.http.ReciptAttributes.MEMBER_INFO
import com.recipt.core.model.MemberInfo
import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.ProfileModifyRequest
import com.recipt.member.presentation.model.request.RefreshTokenRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
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
        private val memberInfo = MemberInfo.TEST_MEMBER_INFO
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

        every { memberQueryService.getProfile(memberNo) } returns mockk()

        val result = runBlocking { memberHandler.getProfile(request) }

        assertEquals(HttpStatus.OK, result.statusCode())
    }

    @Test
    fun `회원 가입`() {
        val body = SignUpRequest(
            email = "email@email.com",
            password = "password",
            name = "홍길동",
            nickname = "nickname",
            mobileNo = "010-1121-1121"
        )

        val request = MockServerRequest.builder()
            .body(Mono.just(body))

        every { memberCommandService.signUp(any()) } returns Mono.just(Unit)

        val result = runBlocking { memberHandler.signUp(request) }

        assertEquals(HttpStatus.CREATED, result.statusCode())
    }

    @Test
    fun `토큰 발급`() {
        val logInRequest = LogInRequest(
            email = "email@email.com",
            password = "password"
        )
        val token = TokenResult(
            "accessToken", "refreshToken"
        )
        val request = MockServerRequest.builder()
            .body(Mono.just(logInRequest))

        every { authenticationService.getToken(logInRequest.toCommand()) } returns Mono.just(token)

        val result = runBlocking { memberHandler.getToken(request) }

        assertEquals(HttpStatus.OK, result.statusCode())
    }

    @Test
    fun `토큰 재발급`() {
        val refreshToken = RefreshTokenRequest("refreshToken")

        val request = MockServerRequest.builder()
            .body(Mono.just(refreshToken))

        every { authenticationService.refreshToken(refreshToken.refreshToken) } returns mockk()

        val result = runBlocking { memberHandler.refreshToken(request) }

        assertEquals(HttpStatus.OK, result.statusCode())

        verify {
            authenticationService.refreshToken(refreshToken.refreshToken)
        }
    }

    @Test
    fun `자신의 프로필 조회`() {
        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .build()

        every { memberQueryService.getMyProfile(memberInfo.no) } returns mockk()

        val result = runBlocking { memberHandler.getMyProfile(request) }

        assertEquals(HttpStatus.OK, result.statusCode())
    }

    @Test
    fun `자신이 팔로우한 회원 조회`() {
        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .build()

        every { memberQueryService.getFollowerProfiles(memberInfo.no) } returns mockk()

        val result = runBlocking { memberHandler.getFollowingProfileList(request) }

        assertEquals(HttpStatus.OK, result.statusCode())
    }

    @Test
    fun `회원정보 변경`() {
        val updateRequest = ProfileModifyRequest(
            password = "password1233!",
            nickname = "닉네임",
            mobileNo = "010-1111-1111",
            introduction = "소개글",
            profileImageUrl = null,
            newPassword = "password1234!"
        )

        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .body(Mono.just(updateRequest))

        every { memberCommandService.modify(any(), any()) } returns Mono.just(Unit)

        val result = runBlocking {
            memberHandler.modifyMyProfile(request)
        }

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode())
    }

    @Test
    fun `팔로잉 확인`() {
        val followerNo = 2
        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .queryParam("followerNo", followerNo.toString())
            .build()

        every {
            memberQueryService.checkFollowing(from = memberInfo.no, to = followerNo)
        } returns Mono.just(true)

        val result = runBlocking {
            memberHandler.checkFollowing(request)
        }

        assertEquals(HttpStatus.OK, result.statusCode())
    }

    @Test
    fun `팔로우`() {
        val followerNo = 2
        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .queryParam("followerNo", followerNo.toString())
            .build()

        every {
            memberCommandService.follow(from = memberInfo.no, to = followerNo)
        } returns Mono.just(Unit)

        val result = runBlocking {
            memberHandler.follow(request)
        }

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode())
    }

    @Test
    fun `언팔로우`() {
        val followerNo = 2
        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .queryParam("followerNo", followerNo.toString())
            .build()

        every {
            memberCommandService.unfollow(from = memberInfo.no, to = followerNo)
        } returns Mono.just(Unit)

        val result = runBlocking {
            memberHandler.unfollow(request)
        }

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode())
    }
}