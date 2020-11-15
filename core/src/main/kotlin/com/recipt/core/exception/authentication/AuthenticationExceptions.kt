package com.recipt.core.exception.authentication

import com.recipt.core.exception.ReciptException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class EmptyTokenException: ReciptException(AuthenticationErrorCode.EMPTY_TOKEN)

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class RefreshTokenNotFoundException: ReciptException(AuthenticationErrorCode.REFRESH_TOKEN_NOT_FOUND)