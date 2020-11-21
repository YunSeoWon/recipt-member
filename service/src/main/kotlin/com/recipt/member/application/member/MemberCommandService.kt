package com.recipt.member.application.member

import com.recipt.member.application.member.dto.ProfileModifyCommand
import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.member.entity.FollowerMapping
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.FollowerMappingRepository
import com.recipt.member.domain.member.repository.MemberRepository
import com.recipt.core.exception.member.DuplicatedMemberException
import com.recipt.core.exception.member.MemberNotFoundException
import com.recipt.core.exception.member.WrongPasswordException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate

@Component
class MemberCommandService(
    private val memberRepository: MemberRepository,
    private val followerMappingRepository: FollowerMappingRepository,
    private val passwordEncoder: PasswordEncoder,
    private val transactionTemplate: TransactionTemplate
) {

    fun signUp(command: SignUpCommand) {
        transactionTemplate.execute {
            memberRepository.findByEmailOrNickname(command.email, command.nickname)?.let {
                if (it.email == command.email) throw DuplicatedMemberException("email")
                if (it.nickname == command.nickname) throw DuplicatedMemberException("nickname")
            }

            Member.create(command).let {
                memberRepository.save(it)
            }
        }
    }

    fun modify(memberNo: Int, command: ProfileModifyCommand) {
        transactionTemplate.execute {
            memberRepository.findByIdOrNull(memberNo)?.let { member ->
                command.newPassword?.let {
                    if (!passwordEncoder.matches(command.password, member.password))
                        throw WrongPasswordException()
                }

                member.modify(command, command.newPassword?.let { passwordEncoder.encode(it) })
                memberRepository.save(member)

            } ?: throw MemberNotFoundException()
        }
    }

    fun follow(from: Int, to: Int) {
        transactionTemplate.execute {
            if (!memberRepository.existsById(to)) throw MemberNotFoundException()
            if (!memberRepository.existFollowing(from, to))
                followerMappingRepository.save(FollowerMapping(memberNo = from, followerNo = to))
        }
    }

    fun unfollow(from: Int, to: Int) {
        if (!memberRepository.existsById(to)) throw MemberNotFoundException()

        transactionTemplate.execute {
            followerMappingRepository.findByMemberNoAndFollowerNo(from, to)?.let {
                followerMappingRepository.deleteById(it.no)
            }
        }
    }
}