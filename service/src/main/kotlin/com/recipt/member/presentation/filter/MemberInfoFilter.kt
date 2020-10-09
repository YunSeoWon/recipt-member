package com.recipt.member.presentation.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.recipt.core.http.ReciptAttributes.MEMBER_INFO
import com.recipt.core.http.ReciptHeaders
import com.recipt.core.http.ReciptHeaders.AUTH_TOKEN
import com.recipt.core.http.ReciptHeaders.MEMBER_INFO_HEADER
import com.recipt.core.http.ReciptHeaders.TEST_AUTH_TOKEN
import com.recipt.core.model.MemberInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class MemberInfoFilter(
    private val objectMapper: ObjectMapper
): WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.request.headers[MEMBER_INFO_HEADER]?.firstOrNull()?.let {
            exchange.attributes[MEMBER_INFO] = objectMapper.readValue(it, MemberInfo::class.java)
        }

        return chain.filter(exchange)
    }
}