package com.recipt.member.presentation.exception.member

import com.recipt.member.presentation.exception.ReciptException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class MemberNotFoundException: ReciptException(MemberErrorCode.NOT_FOUND)