package com.recipt.member.presentation.handler

import com.recipt.core.http.ReciptAttributes.MEMBER_INFO
import com.recipt.core.http.ReciptCookies
import com.recipt.core.model.MemberInfo
import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.ProfileModifyRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpCookie
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
        private val memberInfo = MemberInfo(
            email = "email@email.com",
            no = 1,
            nickname = "nickname"
        )
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

    @Test
    fun `토큰 발급`() {
        val logInRequest = LogInRequest(
            email = "email@email.com",
            password = "password"
        )
        val tokenResult = TokenResult(
            accessToken = ReciptCookies.TEST_ACCESS_TOKEN,
            refreshToken = ReciptCookies.TEST_REFRESH_TOKEN
        )
        val request = MockServerRequest.builder()
            .body(Mono.just(logInRequest))

        every { authenticationService.getToken(logInRequest.toCommand()) } returns tokenResult

        val result = runBlocking { memberHandler.getToken(request) }

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode())

        assertEquals(tokenResult.accessToken, result.cookies().getFirst(ReciptCookies.ACCESS_TOKEN)!!.value)
        assertEquals(tokenResult.refreshToken, result.cookies().getFirst(ReciptCookies.REFRESH_TOKEN)!!.value)
    }

    @Test
    fun `토큰 재발급`() {
        val request = MockServerRequest.builder()
            .cookie(HttpCookie(ReciptCookies.REFRESH_TOKEN, ReciptCookies.TEST_REFRESH_TOKEN))
            .build()

        val tokenResult = TokenResult(
            accessToken = ReciptCookies.TEST_ACCESS_TOKEN,
            refreshToken = ReciptCookies.TEST_REFRESH_TOKEN
        )

        every { authenticationService.refreshToken(any()) } returns tokenResult

        val result = runBlocking { memberHandler.refreshToken(request) }

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode())
        assertEquals(tokenResult.accessToken, result.cookies().getFirst(ReciptCookies.ACCESS_TOKEN)!!.value)
        assertEquals(tokenResult.refreshToken, result.cookies().getFirst(ReciptCookies.REFRESH_TOKEN)!!.value)

        verify {
            authenticationService.refreshToken(any())
        }
    }

    @Test
    fun `자신의 프로필 조회`() {
        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .build()

        coEvery { memberQueryService.getMyProfile(memberInfo.no) } returns mockk()

        val result = runBlocking { memberHandler.getMyProfile(request) }

        assertEquals(HttpStatus.OK, result.statusCode())
    }

    @Test
    fun `자신이 팔로우한 회원 조회`() {
        val request = MockServerRequest.builder()
            .attribute(MEMBER_INFO, memberInfo)
            .build()

        coEvery { memberQueryService.getFollowerProfiles(memberInfo.no) } returns mockk()

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

        coEvery { memberCommandService.modify(any(), any()) } just runs

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

        coEvery { memberQueryService.checkFollowing(from = memberInfo.no, to = followerNo) } returns true

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

        coEvery { memberCommandService.follow(from = memberInfo.no, to = followerNo) } just runs

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

        coEvery { memberCommandService.unfollow(from = memberInfo.no, to = followerNo) } just runs

        val result = runBlocking {
            memberHandler.unfollow(request)
        }

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode())
    }
}