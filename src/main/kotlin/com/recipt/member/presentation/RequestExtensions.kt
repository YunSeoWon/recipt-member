package com.recipt.member.presentation

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull

fun ServerRequest.queryParamToPositiveIntOrThrow(parameterName: String) =
    queryParamOrNull(parameterName)
        ?.toIntOrNull()
        ?.takeIf { it > 0 }?: throw Exception()