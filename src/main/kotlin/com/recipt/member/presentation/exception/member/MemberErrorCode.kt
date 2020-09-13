package com.recipt.member.presentation.exception.member

import com.recipt.member.presentation.exception.ErrorCode

enum class MemberErrorCode(override val code: String): ErrorCode {
    NOT_FOUND("error.member.not-found"),
    DUPLICATED("error.member.duplicated");
}