package com.recipt.member.presentation.exception.request

import com.recipt.member.presentation.exception.ErrorCode

enum class RequestErrorCode(override val code: String): ErrorCode {
    INVALID_PARAMETER("error.request.invalid-parameter")
}