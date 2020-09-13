package com.recipt.member.presentation

import com.recipt.member.presentation.model.request.SignUpRequest
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