package com.recipt.core.enums.authentication

enum class TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN;

    companion object {
        fun getTokenType(refresh: Boolean) = if (refresh) REFRESH_TOKEN else ACCESS_TOKEN
    }
}