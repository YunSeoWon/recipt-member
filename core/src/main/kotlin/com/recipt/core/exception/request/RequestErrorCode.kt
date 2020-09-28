package com.recipt.core.exception.request

import com.recipt.core.exception.ErrorCode

enum class RequestErrorCode(override val code: String): ErrorCode {
    INVALID_PARAMETER("error.request.invalid-parameter"),
    BODY_EXTRACT_FAIL("error.request.body-extract-fail"),
    NO_PERMISSION("error.request.no-permission");
}