package com.recipt.member.application.authentication

import com.recipt.member.application.authentication.dto.TokenCreateCommand
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.infrastructure.security.JwtTokenProvider
import com.recipt.core.exception.member.WrongEmailOrPasswordException
import com.recipt.member.application.authentication.dto.TokenResult
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthenticationService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder
) {
    fun getToken(command: TokenCreateCommand): TokenResult {
        val member = memberRepository.findByEmail(command.email)
            ?.takeIf { passwordEncoder.matches(command.password, it.password) }
            ?: throw WrongEmailOrPasswordException()

        return jwtTokenProvider.generateToken(member)
    }

    fun refreshToken(refreshToken: String): TokenResult {
        return jwtTokenProvider.findAndDelete(refreshToken).let {
            jwtTokenProvider.doGenerateToken(it)
        }
    }
}