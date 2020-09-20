package com.recipt.member.domain.enum

import java.time.format.DateTimeFormatter

enum class FormatEnum(pattern: String) {
    DATETIME("yyyy-MM-dd HH:mm:ss");

    val formatter = DateTimeFormatter.ofPattern(pattern)
}