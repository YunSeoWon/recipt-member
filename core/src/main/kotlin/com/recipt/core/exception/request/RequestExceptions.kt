package com.recipt.core.exception.request

import com.recipt.core.exception.ReciptException
import com.recipt.core.exception.request.RequestErrorCode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class InvalidParameterException(val parameterName: String):
    ReciptException(RequestErrorCode.INVALID_PARAMETER, parameterName)

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class RequestBodyExtractFailedException:
    ReciptException(RequestErrorCode.BODY_EXTRACT_FAIL)

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
class PermissionException:
    ReciptException(RequestErrorCode.NO_PERMISSION)
