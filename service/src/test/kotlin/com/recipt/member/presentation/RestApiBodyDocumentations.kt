package com.recipt.member.presentation

import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.application.member.dto.FollowerProfileSummary
import com.recipt.member.application.member.dto.MyProfile
import com.recipt.member.application.member.dto.ProfileSummary
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.ProfileModifyRequest
import com.recipt.member.presentation.model.request.RefreshTokenRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import com.recipt.member.presentation.model.response.CheckingResponse
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath


fun SignUpRequest.toDocument() = arrayOf(
    fieldWithPath("email").type(JsonFieldType.STRING)
        .description("가입할 회원 이메일"),
    fieldWithPath("password").type(JsonFieldType.STRING)
        .description("가입할 회원 비밀번호, 영문, 숫자, 특수문자 포함 8자 이상 15자 이하"),
    fieldWithPath("nickname").type(JsonFieldType.STRING)
        .description("회원 닉네임"),
    fieldWithPath("mobileNo").type(JsonFieldType.STRING)
        .description("회원 휴대전화 번호")
)

fun LogInRequest.toDocument() = arrayOf(
    fieldWithPath("email").type(JsonFieldType.STRING)
        .description("로그인 회원 이메일"),
    fieldWithPath("password").type(JsonFieldType.STRING)
        .description("로그인 회원 비밀번호")
)

fun RefreshTokenRequest.toDocument() = arrayOf(
    fieldWithPath("refreshToken").type(JsonFieldType.STRING)
        .description("refresh token")
)

fun TokenResult.toDocument() = arrayOf(
    fieldWithPath("accessToken").type(JsonFieldType.STRING)
        .description("RECIPT 인증 TOKEN"),
    fieldWithPath("refreshToken").type(JsonFieldType.STRING)
        .description("RECIPT refresh TOKEN")
)

fun ProfileSummary.toDocument() = arrayOf(
    fieldWithPath("nickname").type(JsonFieldType.STRING)
        .description("닉네임"),
    fieldWithPath("introduction").type(JsonFieldType.STRING)
        .description("회원 소개 글"),
    fieldWithPath("followerCount").type(JsonFieldType.NUMBER)
        .description("팔로워 수"),
    fieldWithPath("totalRecipeReadCount").type(JsonFieldType.NUMBER)
        .description("회원이 쓴 레시피 총 조회 수"),
    fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).optional()
        .description("회원 프로필 이미지")
)

fun MyProfile.toDocument() = arrayOf(
    fieldWithPath("email").type(JsonFieldType.STRING)
        .description("이메일"),
    fieldWithPath("nickname").type(JsonFieldType.STRING)
        .description("닉네임"),
    fieldWithPath("mobileNo").type(JsonFieldType.STRING)
        .description("휴대전화 번호"),
    fieldWithPath("introduction").type(JsonFieldType.STRING)
        .description("회원 소개 글"),
    fieldWithPath("followerCount").type(JsonFieldType.NUMBER)
        .description("팔로워 수"),
    fieldWithPath("totalRecipeReadCount").type(JsonFieldType.NUMBER)
        .description("회원이 쓴 레시피 총 조회 수"),
    fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).optional()
        .description("회원 프로필 이미지")
)

fun FollowerProfileSummary.toDocument(prefix: String = "") = arrayOf(
    fieldWithPath("$prefix.nickname").type(JsonFieldType.STRING)
        .description("닉네임"),
    fieldWithPath("$prefix.profileImageUrl").type(JsonFieldType.STRING).optional()
        .description("회원 프로필 이미지")
)

fun ProfileModifyRequest.toDocument(prefix: String = "") = arrayOf(
    fieldWithPath("$prefix.password").type(JsonFieldType.STRING)
        .description("패스워드"),
    fieldWithPath("$prefix.nickname").type(JsonFieldType.STRING).optional()
        .description("닉네임"),
    fieldWithPath("$prefix.mobileNo").type(JsonFieldType.STRING).optional()
        .description("휴대전화 번호"),
    fieldWithPath("$prefix.introduction").type(JsonFieldType.STRING).optional()
        .description("소개글"),
    fieldWithPath("$prefix.profileImageUrl").type(JsonFieldType.STRING).optional()
        .description("회원 프로필 이미지"),
    fieldWithPath("$prefix.newPassword").type(JsonFieldType.STRING).optional()
        .description("새 비밀번호")
)

fun CheckingResponse.toDocument(what: String) = arrayOf(
    fieldWithPath("right").type(JsonFieldType.BOOLEAN)
        .description("$what 여부")
)