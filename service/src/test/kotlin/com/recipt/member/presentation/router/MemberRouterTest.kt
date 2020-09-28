package com.recipt.member.presentation.router

import com.ninjasquad.springmockk.MockkBean
import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.authentication.dto.TokenCreateCommand
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.application.member.dto.*
import com.recipt.core.http.ReciptHeaders.AUTH_TOKEN
import com.recipt.core.http.ReciptHeaders.TEST_AUTH_TOKEN
import com.recipt.member.presentation.handler.MemberHandler
import com.recipt.core.model.MemberInfo
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.ProfileModifyRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import com.recipt.member.presentation.model.response.CheckingResponse
import com.recipt.member.presentation.model.response.TokenResponse
import com.recipt.member.presentation.support.TestSecurityConfig
import com.recipt.member.presentation.toDocument
import com.recipt.member.presentation.tokenHeader
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import javax.validation.Validator


@WebFluxTest
@ExtendWith(RestDocumentationExtension::class)
@ContextConfiguration(classes = [MemberRouter::class, MemberHandler::class, TestSecurityConfig::class])
internal class MemberRouterTest {

    @MockkBean
    private lateinit var memberQueryService: MemberQueryService

    @MockkBean
    private lateinit var memberCommandService: MemberCommandService

    @MockkBean
    private lateinit var validator: Validator

    @MockkBean
    private lateinit var passwordEncoder: PasswordEncoder

    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    private lateinit var webTestClient: WebTestClient

    companion object {
        private const val ENCODED = "E@DDF$#@T"
    }

    @BeforeEach
    fun setUp(
        applicationContext: ApplicationContext,
        restDocumentation: RestDocumentationContextProvider
    ) {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
            .configureClient()
            .filter(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withResponseDefaults(prettyPrint()))
            .build()

        every { validator.validate(any<Any>()) } returns emptySet()
        every { passwordEncoder.encode(any()) } returns ENCODED
    }

    @Test
    fun `회원번호로 프로필 조회`() {
        val memberNo = 1
        val summary = ProfileSummary(
            nickname = "테스터",
            introduction = "테스트",
            followerCount = 1,
            totalRecipeReadCount = 0,
            profileImageUrl = "http://image.com"
        )

        coEvery { memberQueryService.getProfile(memberNo) } returns summary

        webTestClient.get()
            .uri("/members/profiles/{memberNo}", memberNo)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                document(
                    "member-profile",
                    pathParameters(
                       parameterWithName("memberNo").description("회원 번호")
                    ),
                    responseFields(*summary.toDocument())
                )
            )
    }

    @Test
    fun `회원가입`() {
        val request = SignUpRequest(
            email = "email@email.com",
            nickname = "홍길동",
            password = "abcd1234!",
            mobileNo = "010-1234-5678"
        )

        coEvery { memberCommandService.signUp(any<SignUpCommand>()) } just runs

        webTestClient.post()
            .uri("/members")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody().consumeWith(
                document(
                    "signup",
                    requestFields(*request.toDocument())
                )
            )
    }

    @Test
    fun `토큰 발급`() {
        val request = LogInRequest(
            email = "email@email.com",
            password = "password1234!"
        )

        val response = TokenResponse(
            token = "token"
        )

        coEvery { authenticationService.getToken(any<TokenCreateCommand>()) } returns response.token

        webTestClient.post()
            .uri("/members/token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                document(
                    "issue-token",
                    requestFields(*request.toDocument()),
                    responseFields(*response.toDocument())
                )
            )
    }

    @Test
    fun `내 프로필 조회`() {
        val response = MyProfile(
            email = "email@email.com",
            nickname = "nickname",
            introduction = "intro",
            mobileNo = "010-1234-5678",
            followerCount = 1,
            totalRecipeReadCount = 1,
            profileImageUrl = "http://image.com"
        )

        coEvery { memberQueryService.getMyProfile(MemberInfo.TEST_MEMBER_INFO.no) } returns response

        webTestClient.get()
            .uri("/members/profiles/me")
            .accept(MediaType.APPLICATION_JSON)
            .header(AUTH_TOKEN, TEST_AUTH_TOKEN)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                document(
                    "get-my-profile",
                    requestHeaders(*tokenHeader),
                    responseFields(*response.toDocument())
                )
            )
    }

    @Test
    fun `프로필 변경`() {
        val updateRequest = ProfileModifyRequest(
            nickname = "nickname",
            introduction = "intro",
            mobileNo = "010-1234-5678",
            profileImageUrl = "http://image.com",
            password = "password1234!",
            newPassword = "password1235!"
        )

        every { memberCommandService.modify(MemberInfo.TEST_MEMBER_INFO.no, updateRequest.toCommand()) } just runs

        webTestClient.put()
            .uri("/members/profiles/me")
            .accept(MediaType.APPLICATION_JSON)
            .header(AUTH_TOKEN, TEST_AUTH_TOKEN)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isNoContent
            .expectBody().consumeWith(
                document(
                    "modify-my-profile",
                    requestHeaders(*tokenHeader),
                    requestFields(*updateRequest.toDocument())
                )
            )
    }

    @Test
    fun `자신이 팔로우한 회원 조회`() {
        val response = listOf(
            FollowerProfileSummary(
                "팔로워",
                "http://image-url.com"
            )
        )

        coEvery { memberQueryService.getFollowerProfiles(MemberInfo.TEST_MEMBER_INFO.no) } returns response

        webTestClient.get()
            .uri("/members/following")
            .accept(MediaType.APPLICATION_JSON)
            .header(AUTH_TOKEN, TEST_AUTH_TOKEN)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                document(
                    "get-follower-list",
                    requestHeaders(*tokenHeader),
                    responseFields(*response[0].toDocument("[]"))
                )
            )
    }

    @Test
    fun `팔로우 체크`() {
        val followerNo = 3
        val response = CheckingResponse(true)
        coEvery { memberQueryService.checkFollowing(any(), any()) } returns response.right

        webTestClient.get()
            .uri("/members/following/check?followerNo=${followerNo}")
            .accept(MediaType.APPLICATION_JSON)
            .header(AUTH_TOKEN, TEST_AUTH_TOKEN)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                document(
                    "check-follower-list",
                    requestHeaders(*tokenHeader),
                    responseFields(*response.toDocument("팔로우"))
                )
            )
    }

    @Test
    fun `팔로우`() {
        val followerNo = 3

        coEvery { memberCommandService.follow(any(), any()) } just runs

        webTestClient.post()
            .uri("/members/following?followerNo=${followerNo}")
            .accept(MediaType.APPLICATION_JSON)
            .header(AUTH_TOKEN, TEST_AUTH_TOKEN)
            .exchange()
            .expectStatus().isNoContent
            .expectBody().consumeWith(
                document(
                    "follow",
                    requestHeaders(*tokenHeader)
                )
            )
    }
    @Test
    fun `언팔로우`() {
        val followerNo = 3

        coEvery { memberCommandService.unfollow(any(), any()) } just runs

        webTestClient.delete()
            .uri("/members/following?followerNo=${followerNo}")
            .accept(MediaType.APPLICATION_JSON)
            .header(AUTH_TOKEN, TEST_AUTH_TOKEN)
            .exchange()
            .expectStatus().isNoContent
            .expectBody().consumeWith(
                document(
                    "unfollow",
                    requestHeaders(*tokenHeader)
                )
            )
    }
}