package com.recipt.member.application.member

import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.presentation.exception.member.DuplicatedMemberException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MemberCommandService(
    private val memberRepository: MemberRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun signUp(command: SignUpCommand) {
        logger.info("SIGN UP: command: $command")
        memberRepository.findByEmailOrNickname(command.email, command.nickname)?.let {
            if (it.email == command.email) throw DuplicatedMemberException("email")
            if (it.nickname == command.nickname) throw DuplicatedMemberException("nickname")
        }

        Member.create(command).let {
            memberRepository.save(it)
        }
    }
}