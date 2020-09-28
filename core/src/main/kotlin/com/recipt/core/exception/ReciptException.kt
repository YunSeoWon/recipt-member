package com.recipt.core.exception

import com.recipt.core.exception.ErrorCode

abstract class ReciptException(val errorCode: ErrorCode, vararg args: Any): RuntimeException()