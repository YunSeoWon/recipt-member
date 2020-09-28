package com.recipt.core.exception.member

import com.recipt.core.exception.ReciptException
import com.recipt.core.exception.member.MemberErrorCode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class MemberNotFoundException: ReciptException(MemberErrorCode.NOT_FOUND)

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class DuplicatedMemberException(something: String): ReciptException(MemberErrorCode.DUPLICATED, something)

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class WrongEmailOrPasswordException: ReciptException(MemberErrorCode.WRONG_EMAIL_OR_PASSWORD)

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class WrongPasswordException: ReciptException(MemberErrorCode.WRONG_PASSWORD)