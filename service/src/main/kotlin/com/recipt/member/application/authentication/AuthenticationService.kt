package com.recipt.member.application.authentication

import com.recipt.core.exception.member.MemberNotFoundException
import com.recipt.core.exception.member.WrongEmailOrPasswordException
import com.recipt.core.extensions.filterOrError
import com.recipt.member.application.authentication.dto.TokenCreateCommand
import com.recipt.member.application.authentication.dto.TokenResult
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.infrastructure.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class AuthenticationService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder
) {
    fun getToken(command: TokenCreateCommand): Mono<TokenResult> {
        return Mono.fromCallable {
            memberRepository.findByEmail(command.email) ?: throw MemberNotFoundException()
        }
            .filterOrError(WrongEmailOrPasswordException()) {
                passwordEncoder.matches(command.password, it.password)
            }
            .flatMap { jwtTokenProvider.generateToken(it) }
            .subscribeOn(Schedulers.elastic())

    }

    fun refreshToken(refreshToken: String): Mono<TokenResult> {
        return jwtTokenProvider.findAndDelete(refreshToken)
            .flatMap { jwtTokenProvider.doGenerateToken(it) }
            .subscribeOn(Schedulers.elastic())
    }
}