package com.recipt.core.enums

enum class RedisKeyEnum(val key: String) {
    REFRESH_TOKEN("recipt:refresh-token");

    fun getKey(vararg data: String) = "$key:${data.joinToString(":")}"
}