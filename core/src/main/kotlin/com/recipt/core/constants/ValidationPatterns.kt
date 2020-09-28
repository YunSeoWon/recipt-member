package com.recipt.core.constants

import java.util.regex.Pattern

object ValidationPatterns {
    /** 최소 하나의 문자 및 하나의 숫자 및 특수문자 [8~15자리] **/
    const val PASSWORD = "^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$"


    const val PHONE = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$"
}

fun checkPatternMatch(pattern: String, str: String): Boolean {
    return Pattern.matches(pattern, str)
}