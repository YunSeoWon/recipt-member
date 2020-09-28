package com.recipt.member.presentation

import com.recipt.core.http.ReciptAttributes.MEMBER_INFO
import com.recipt.core.exception.request.InvalidParameterException
import com.recipt.core.exception.request.PermissionException
import com.recipt.core.model.MemberInfo
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