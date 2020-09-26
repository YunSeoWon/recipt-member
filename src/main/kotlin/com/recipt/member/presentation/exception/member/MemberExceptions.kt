package com.recipt.member.presentation.exception.member

import com.recipt.member.presentation.exception.ReciptException
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