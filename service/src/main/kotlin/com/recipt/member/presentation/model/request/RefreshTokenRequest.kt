package com.recipt.member.presentation.model.request

import com.recipt.core.exception.authentication.EmptyTokenException

data class RefreshTokenRequest(
    val refreshToken: String
) {
    fun validate() {
        if (refreshToken.isBlank()) throw EmptyTokenException()
    }
}