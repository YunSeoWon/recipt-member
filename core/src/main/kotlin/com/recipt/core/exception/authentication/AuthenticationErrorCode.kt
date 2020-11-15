package com.recipt.core.exception.authentication

import com.recipt.core.exception.ErrorCode

enum class AuthenticationErrorCode(override val code: String): ErrorCode {
    EMPTY_TOKEN("error.auth.empty-token"),
    REFRESH_TOKEN_NOT_FOUND("error.auth.refresh-token-not-found"),
    ;
}