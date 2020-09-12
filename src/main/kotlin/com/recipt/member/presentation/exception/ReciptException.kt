package com.recipt.member.presentation.exception

abstract class ReciptException(val errorCode: ErrorCode, vararg args: Any): RuntimeException()