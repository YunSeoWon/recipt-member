package com.recipt.member.presentation.handler

import com.recipt.member.application.authentication.AuthenticationService
import com.recipt.member.application.member.MemberCommandService
import com.recipt.member.application.member.MemberQueryService
import com.recipt.member.presentation.awaitBodyOrThrow
import com.recipt.member.presentation.memberInfoOrThrow
import com.recipt.member.presentation.model.request.LogInRequest
import com.recipt.member.presentation.model.request.ProfileModifyRequest
import com.recipt.member.presentation.model.request.RefreshTokenRequest
import com.recipt.member.presentation.model.request.SignUpRequest
import com.recipt.member.presentation.model.response.CheckingResponse
import com.recipt.member.presentation.pathVariableToPositiveIntOrThrow
import com.recipt.member.presentation.queryParamToPositiveIntOrThrow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.buildAndAwait
import java.net.URI
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

        return memberQueryService.getProfile(memberNo)
            .let { ok().body(it).awaitSingle() }
    }

    suspend fun getMyProfile(request: ServerRequest): ServerResponse {
        val memberInfo = request.memberInfoOrThrow()

        return memberQueryService.getMyProfile(memberInfo.no)
            .let { ok().body(it).awaitSingle() }
    }

    suspend fun getFollowingProfileList(request: ServerRequest): ServerResponse {
        val memberInfo = request.memberInfoOrThrow()

        return memberQueryService.getFollowerProfiles(memberInfo.no)
            .let { ok().body(it).awaitSingle() }
    }

    suspend fun modifyMyProfile(request: ServerRequest): ServerResponse {
        val memberInfo = request.memberInfoOrThrow()
        val modifyRequest = request.awaitBodyOrThrow<ProfileModifyRequest>()
            .also {
                validator.validate(it)
                it.validate()
            }

        memberCommandService.modify(memberInfo.no, modifyRequest.toCommand())
            .awaitSingle()

        return noContent().buildAndAwait()
    }

    suspend fun signUp(request: ServerRequest): ServerResponse {
        val signUpRequest = request.awaitBodyOrThrow<SignUpRequest>()
            .also { validator.validate(it) }

        memberCommandService.signUp(signUpRequest.toCommand(passwordEncoder))
            .awaitSingle()

        return created(REDIRECTION_URL).buildAndAwait()
    }

    suspend fun getToken(request: ServerRequest): ServerResponse {
        val logInRequest = request.awaitBodyOrThrow<LogInRequest>()
            .also { validator.validate(it) }

        return authenticationService.getToken(logInRequest.toCommand())
            .let { ok().body(it).awaitSingle() }
    }

    suspend fun refreshToken(request: ServerRequest): ServerResponse {
        val refreshTokenRequest = request.awaitBodyOrThrow<RefreshTokenRequest>()
            .also { it.validate() }

        return authenticationService.refreshToken(refreshTokenRequest.refreshToken)
            .let { ok().body(it).awaitSingle() }
    }

    suspend fun checkFollowing(request: ServerRequest): ServerResponse {
        val memberNo = request.queryParamToPositiveIntOrThrow("followerNo")
        val memberInfo = request.memberInfoOrThrow()

        return memberQueryService.checkFollowing(from = memberInfo.no, to = memberNo)
            .map { CheckingResponse(it) }
            .let { ok().body(it) }
            .awaitSingle()
    }

    suspend fun follow(request: ServerRequest): ServerResponse {
        val memberNo = request.queryParamToPositiveIntOrThrow("followerNo")
        val memberInfo = request.memberInfoOrThrow()

        memberCommandService.follow(from = memberInfo.no, to = memberNo)
            .awaitSingle()

        return noContent().buildAndAwait()
    }

    suspend fun unfollow(request: ServerRequest): ServerResponse {
        val memberNo = request.queryParamToPositiveIntOrThrow("followerNo")
        val memberInfo = request.memberInfoOrThrow()

        memberCommandService.unfollow(from = memberInfo.no, to = memberNo)
            .awaitSingle()
        
        return noContent().buildAndAwait()
    }
}