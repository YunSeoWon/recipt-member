package com.recipt.member.presentation

object ValidationPatterns {
    const val PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,15}$"

    const val PHONE = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$"
}