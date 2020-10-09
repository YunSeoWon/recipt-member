package com.recipt.member.application.authentication

import com.recipt.core.enums.RedisKeyEnum
import com.recipt.member.application.authentication.dto.TokenCreateCommand
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.infrastructure.security.JwtTokenProvider
import com.recipt.core.exception.member.WrongEmailOrPasswordException
import org.springframework.data.redis.core.RedisKeyExpiredEvent
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthenticationService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val stringRedisTemplate: StringRedisTemplate
) {
    fun getToken(command: TokenCreateCommand): String {
        val member = memberRepository.findByEmail(command.email)
            ?.takeIf { passwordEncoder.matches(command.password, it.password) }
            ?: throw WrongEmailOrPasswordException()

        return jwtTokenProvider.generateToken(member).also {
            stringRedisTemplate.opsForValue()
                .set(RedisKeyEnum.TOKEN.getKey(command.email), it)
        }
    }
}