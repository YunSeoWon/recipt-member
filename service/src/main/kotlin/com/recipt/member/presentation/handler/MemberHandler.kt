package com.recipt.member.presentation.handler

import com.recipt.core.exception.authentication.RefreshTokenNotFoundException
import com.recipt.core.http.ReciptCookies
import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.presentation.awaitBodyOrThrow
import com.recipt.member.presentation.memberInfoOrThrow
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.ProfileModifyRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import com.recipt.member.presentation.model.response.CheckingResponse
import com.recipt.member.presentation.pathVariableToPositiveIntOrThrow
import com.recipt.member.presentation.queryParamToPositiveIntOrThrow
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import java.net.URI
import java.time.Duration
import javax.validation.Validator

@Component
class MemberHandler(
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
        val memberInfo = request.memberInfoOrThrow()
        val modifyRequest = request.awaitBodyOrThrow<ProfileModifyRequest>()
            .also {
                validator.validate(it)
                it.validate()
            }

        memberCommandService.modify(memberInfo.no, modifyRequest.toCommand())

        return noContent().buildAndAwait()
    }

    suspend fun signUp(request: ServerRequest): ServerResponse {
        val signUpRequest = request.awaitBodyOrThrow<SignUpRequest>()
            .also { validator.validate(it) }

        memberCommandService.signUp(signUpRequest.toCommand(passwordEncoder))

        return created(REDIRECTION_URL).buildAndAwait()
    }

    suspend fun getToken(request: ServerRequest): ServerResponse {
        val logInRequest = request.awaitBodyOrThrow<LogInRequest>()
            .also { validator.validate(it) }

        return setToken(authenticationService.getToken(logInRequest.toCommand()))
    }

    suspend fun refreshToken(request: ServerRequest): ServerResponse {
        val refreshToken = request.cookies().getFirst(ReciptCookies.REFRESH_TOKEN)
            ?.value
            ?: throw RefreshTokenNotFoundException()

        return setToken(authenticationService.refreshToken(refreshToken))
    }

    suspend fun checkFollowing(request: ServerRequest): ServerResponse {
        val memberNo = request.queryParamToPositiveIntOrThrow("followerNo")
        val memberInfo = request.memberInfoOrThrow()

        return memberQueryService.checkFollowing(from = memberInfo.no, to = memberNo).let {
            ok().bodyValueAndAwait(CheckingResponse(it))
        }
    }

    suspend fun follow(request: ServerRequest): ServerResponse {
        val memberNo = request.queryParamToPositiveIntOrThrow("followerNo")
        val memberInfo = request.memberInfoOrThrow()

        memberCommandService.follow(from = memberInfo.no, to = memberNo)
        return noContent().buildAndAwait()
    }

    suspend fun unfollow(request: ServerRequest): ServerResponse {
        val memberNo = request.queryParamToPositiveIntOrThrow("followerNo")
        val memberInfo = request.memberInfoOrThrow()

        memberCommandService.unfollow(from = memberInfo.no, to = memberNo)
        return noContent().buildAndAwait()
    }

    private suspend fun setToken(token: TokenResult): ServerResponse {
        val accessTokenCookie = ResponseCookie.from("accessToken", token.accessToken)
            .domain("localhost")
            .maxAge(Duration.ofMinutes(30L))
            .httpOnly(true)
            .build()

        val refreshTokenCookie = ResponseCookie.from("refreshToken", token.refreshToken)
            .domain("localhost")
            .maxAge(Duration.ofDays(7))
            .httpOnly(true)
            .build()

        return noContent().cookies {
            it.add(ReciptCookies.ACCESS_TOKEN, accessTokenCookie)
            it.add(ReciptCookies.REFRESH_TOKEN, refreshTokenCookie)
        }.buildAndAwait()
    }
}