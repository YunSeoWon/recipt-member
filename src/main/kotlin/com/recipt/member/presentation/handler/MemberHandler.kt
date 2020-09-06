package com.recipt.member.presentation.handler

import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.presentation.queryParamToPositiveIntOrThrow
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class MemberHandler (
    private val memberQueryService: MemberQueryService
) {

    suspend fun getProfile(request: ServerRequest): ServerResponse {
        val memberNo = request.queryParamToPositiveIntOrThrow("memberNo")

        return ok().bodyValueAndAwait(memberQueryService.getProfile(memberNo))
    }

    suspend fun getMyProfile(request: ServerRequest): ServerResponse {
        // TODO: 인증 토큰을 만들어야..

        return ok().bodyValueAndAwait("")
    }

    suspend fun getFollowingProfileList(request: ServerRequest): ServerResponse {
        // TODO
        return ok().bodyValueAndAwait("")
    }

    suspend fun modifyMyProfile(request: ServerRequest): ServerResponse {
        // TODO
        return ok().bodyValueAndAwait("")
    }

    suspend fun signUp(request: ServerRequest): ServerResponse {
        // TODO
        return ok().bodyValueAndAwait("")
    }

    suspend fun checkFollowing(request: ServerRequest): ServerResponse {
        // TODO
        return ok().bodyValueAndAwait("")
    }

    suspend fun follow(request: ServerRequest): ServerResponse {
        // TODO
        return ok().bodyValueAndAwait("")
    }

    suspend fun unfollow(request: ServerRequest): ServerResponse {
        // TODO
        return ok().bodyValueAndAwait("")
    }
}