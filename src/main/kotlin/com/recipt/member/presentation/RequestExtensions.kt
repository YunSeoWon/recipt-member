package com.recipt.member.presentation

import com.recipt.member.presentation.ReciptAttributes.MEMBER_INFO
import com.recipt.member.presentation.exception.request.InvalidParameterException
import com.recipt.member.presentation.exception.request.PermissionException
import com.recipt.member.presentation.model.MemberInfo
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.attributeOrNull
import org.springframework.web.reactive.function.server.queryParamOrNull

fun ServerRequest.queryParamToPositiveIntOrThrow(parameterName: String) =
    queryParamOrNull(parameterName)
        ?.toIntOrNull()
        ?.takeIf { it > 0 }?: throw InvalidParameterException(parameterName)

fun ServerRequest.pathVariableToPositiveIntOrThrow(parameterName: String) =
    pathVariable(parameterName)
        .toIntOrNull()
        ?.takeIf { it > 0 }?: throw InvalidParameterException(parameterName)

fun ServerRequest.memberInfoOrThrow() = (attributeOrNull(MEMBER_INFO) as? MemberInfo)
    ?: throw PermissionException()