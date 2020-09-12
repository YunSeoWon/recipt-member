package com.recipt.member.presentation.router

import com.ninjasquad.springmockk.MockkBean
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.application.member.dto.ProfileSummary
import com.recipt.member.presentation.handler.MemberHandler
import io.mockk.coEvery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient


@WebFluxTest
@ExtendWith(RestDocumentationExtension::class)
@ContextConfiguration(classes = [MemberRouter::class, MemberHandler::class])
internal class MemberRouterTest {

    @MockkBean
    private lateinit var memberQueryService: MemberQueryService

    private lateinit var webTestClient: WebTestClient

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
    }

    @Test
    fun getProfile() {
        val memberNo = 1
        val summary = ProfileSummary(
            nickname = "테스터",
            introduction = "테스트",
            followerCount = 1,
            totalRecipeReadCount = 0
        )

        coEvery { memberQueryService.getProfile(memberNo) } returns summary

        webTestClient.get()
            .uri("/members/profile/{memberNo}", memberNo)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                document(
                    "member-profile",
                    pathParameters(
                       parameterWithName("memberNo").description("회원 번호")
                    ),
                    responseFields(
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                            .description("닉네임"),
                        fieldWithPath("introduction").type(JsonFieldType.STRING)
                            .description("회원 소개 글"),
                        fieldWithPath("followerCount").type(JsonFieldType.NUMBER)
                            .description("팔로워 수"),
                        fieldWithPath("totalRecipeReadCount").type(JsonFieldType.NUMBER)
                            .description("회원이 쓴 레시피 총 조회 수")
                    )
                )
            )
    }
}