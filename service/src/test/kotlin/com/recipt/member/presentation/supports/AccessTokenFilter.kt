package com.recipt.member.presentation.supports

import com.recipt.core.http.ReciptAttributes.MEMBER_INFO
import com.recipt.core.http.ReciptCookies.ACCESS_TOKEN
import com.recipt.core.http.ReciptCookies.TEST_ACCESS_TOKEN
import com.recipt.core.model.MemberInfo
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AccessTokenFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.request.cookies[ACCESS_TOKEN]?.firstOrNull()?.value?.let {
            if (it == TEST_ACCESS_TOKEN) {
                exchange.attributes[MEMBER_INFO] = MemberInfo.TEST_MEMBER_INFO
            }
        }

        return chain.filter(exchange)
    }
}