package com.recipt.member.presentation.router

import com.ninjasquad.springmockk.MockkBean
import com.recipt.core.http.ReciptHeaders.AUTH_TOKEN
import com.recipt.core.http.ReciptHeaders.TEST_AUTH_TOKEN
import com.recipt.core.model.MemberInfo
import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.authentication.dto.TokenCreateCommand
import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.application.member.dto.FollowerProfileSummary
import com.recipt.member.application.member.dto.MyProfile
import com.recipt.member.application.member.dto.ProfileSummary
import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.presentation.exception.GlobalErrorAttributes
import com.recipt.member.presentation.exception.GlobalErrorWebExceptionHandler
import com.recipt.member.presentation.handler.MemberHandler
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.ProfileModifyRequest
import com.recipt.member.presentation.model.request.RefreshTokenRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import com.recipt.member.presentation.model.response.CheckingResponse
import com.recipt.member.presentation.supports.AccessTokenFilter
import com.recipt.member.presentation.toDocument
import com.recipt.member.presentation.tokenHeader
import io.mockk.every
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
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Validator


@WebFluxTest
@ExtendWith(RestDocumentationExtension::class)
@ContextConfiguration(classes = [MemberRouter::class, MemberHandler::class, AccessTokenFilter::class, GlobalErrorWebExceptionHandler::class, GlobalErrorAttributes::class])
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

        every { memberQueryService.getProfile(memberNo) } returns Mono.just(summary)

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

        every {
            memberCommandService.signUp(any<SignUpCommand>())
        } returns Mono.just(Unit)

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

        val response = TokenResult(
            accessToken = "accesstoken",
            refreshToken = "refreshToken"
        )

        every {
            authenticationService.getToken(any<TokenCreateCommand>())
        } returns Mono.just(response)

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
    fun `토큰 재발급`() {
        val request = RefreshTokenRequest(
            "refreshToken"
        )

        val response = TokenResult(
            accessToken = "accesstoken",
            refreshToken = "refreshToken"
        )

        every {
            authenticationService.refreshToken(request.refreshToken)
        } returns Mono.just(response)

        webTestClient.post()
            .uri("/members/token/refresh")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                document(
                    "issue-refresh-token",
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

        every {
            memberQueryService.getMyProfile(MemberInfo.TEST_MEMBER_INFO.no)
        } returns Mono.just(response)

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

        every {
            memberCommandService.modify(MemberInfo.TEST_MEMBER_INFO.no, updateRequest.toCommand())
        } returns Mono.just(Unit)

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

        every {
            memberQueryService.getFollowerProfiles(MemberInfo.TEST_MEMBER_INFO.no)
        } returns Flux.fromIterable(response)

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

        every {
            memberQueryService.checkFollowing(any(), any())
        } returns Mono.just(response.right)

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

        every {
            memberCommandService.follow(any(), any())
        } returns Mono.just(Unit)

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

        every {
            memberCommandService.unfollow(any(), any())
        } returns Mono.just(Unit)

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