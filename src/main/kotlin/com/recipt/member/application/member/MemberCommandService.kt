package com.recipt.member.application.member

import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.member.presentation.exception.member.DuplicatedMemberException
import org.springframework.stereotype.Component

@Component
class MemberCommandService(
    private val memberRepository: MemberRepository
) {

    fun signUp(command: SignUpCommand) {
        memberRepository.findTopByEmailOrNickname(command.email, command.nickname)?.let {
            if (it.email == command.email) throw DuplicatedMemberException("email")
            if (it.nickname == command.nickname) throw DuplicatedMemberException("nickname")
        }

        Member.create(command).let {
            memberRepository.save(it)
        }
    }
}