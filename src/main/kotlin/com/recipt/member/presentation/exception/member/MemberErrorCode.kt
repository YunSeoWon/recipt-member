package com.recipt.member.presentation.exception.member

import com.recipt.member.presentation.exception.ErrorCode

enum class MemberErrorCode(override val code: String): ErrorCode {
    NOT_FOUND("error.member.not-found"),
    DUPLICATED("error.member.duplicated"),
    WRONG_EMAIL_OR_PASSWORD("error.member.wrong-email-or-password"),
    WRONG_PASSWORD("error.member.wrong-password");
}