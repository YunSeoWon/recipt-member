package com.recipt.member.presentation.handler

import com.recipt.member.application.member.MemberQueryService
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest

@ExtendWith(MockKExtension::class)
internal class MemberHandlerTest {
    @MockK
    private lateinit var memberQueryService: MemberQueryService

    private lateinit var memberHandler: MemberHandler

    @BeforeEach
    fun setUp() {
        memberHandler = MemberHandler(memberQueryService)
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
}