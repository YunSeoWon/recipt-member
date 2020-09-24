package com.recipt.member.presentation.handler

import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.presentation.ReciptAttributes.MEMBER_INFO
import com.recipt.member.presentation.exception.request.RequestBodyExtractFailedException
import com.recipt.member.presentation.memberInfoOrThrow
import com.recipt.member.presentation.model.MemberInfo
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import com.recipt.member.presentation.model.response.TokenResponse
import com.recipt.member.presentation.pathVariableToPositiveIntOrThrow
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import java.net.URI
import javax.validation.Validator

@Component
class MemberHandler (
    private val memberQueryService: MemberQueryService,
    private val memberCommandService: MemberCommandService,
    private val validator: Validator,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationService: AuthenticationService
) {

    companion object {
        private val REDIRECTION_URL = URI("https://www.naver.com")  // TODO: Front가 만들어지면 새로 넣기
    }

    suspend fun getProfile(request: ServerRequest): ServerResponse {
        val memberNo = request.pathVariableToPositiveIntOrThrow("memberNo")

        return ok().bodyValueAndAwait(memberQueryService.getProfile(memberNo))
    }

    suspend fun getMyProfile(request: ServerRequest): ServerResponse {
        val memberInfo = request.memberInfoOrThrow()

        return ok().bodyValueAndAwait(memberQueryService.getMyProfile(memberInfo.no))
    }

    suspend fun getFollowingProfileList(request: ServerRequest): ServerResponse {
        val memberInfo = request.memberInfoOrThrow()

        return ok().bodyValueAndAwait(
            memberQueryService.getFollowerProfiles(memberInfo.no)
        )
    }

    suspend fun modifyMyProfile(request: ServerRequest): ServerResponse {
        // TODO 인증 토큰을 만들어야..
        return ok().bodyValueAndAwait("")
    }

    suspend fun signUp(request: ServerRequest): ServerResponse {
        val signUpRequest = request.awaitBodyOrNull<SignUpRequest>()
            ?.also { validator.validate(it) }
            ?: throw RequestBodyExtractFailedException()

        memberCommandService.signUp(signUpRequest.toCommand(passwordEncoder))

        return created(REDIRECTION_URL).buildAndAwait()
    }

    suspend fun getToken(request: ServerRequest): ServerResponse {
        val logInRequest = request.awaitBodyOrNull<LogInRequest>()
            ?.also { validator.validate(it) }
            ?: throw RequestBodyExtractFailedException()

        return authenticationService.getToken(logInRequest.toCommand()).let {
            ok().bodyValueAndAwait(TokenResponse(it))
        }
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