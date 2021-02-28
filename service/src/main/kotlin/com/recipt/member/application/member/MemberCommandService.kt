package com.recipt.member.application.member

import com.recipt.core.exception.member.DuplicatedMemberException
import com.recipt.core.exception.member.MemberNotFoundException
import com.recipt.core.exception.member.WrongPasswordException
import com.recipt.member.application.member.dto.ProfileModifyCommand
import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.member.entity.FollowerMapping
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.FollowerMappingRepository
import com.recipt.member.domain.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono

@Component
class MemberCommandService(
    private val memberRepository: MemberRepository,
    private val followerMappingRepository: FollowerMappingRepository,
    private val passwordEncoder: PasswordEncoder,
    private val transactionTemplate: TransactionTemplate
) {

    // Transaction은?
    fun signUp(command: SignUpCommand): Mono<Unit> {
        return memberRepository.findFirstByEmailOrNickname(command.email, command.nickname)
            .switchIfEmpty(Mono.error(MemberNotFoundException()))
            .handle<Member> { member, sink ->
                when {
                    member.email == command.email -> sink.error(DuplicatedMemberException("email"))
                    member.nickname == command.nickname -> sink.error(DuplicatedMemberException("nickname"))
                    else -> sink.next(Member.create(command))
                }
            }.flatMap { memberRepository.save(it) }
            .then(Mono.just(Unit))
    }

    fun modify(memberNo: Int, command: ProfileModifyCommand): Mono<Unit> {
        return memberRepository.findById(memberNo)
            .switchIfEmpty(Mono.error(MemberNotFoundException()))
            .handle<Member> { member, sink ->
                command.newPassword?.let {
                    if (!passwordEncoder.matches(command.password, member.password))
                        sink.error(WrongPasswordException())
                }
                sink.next(member)
            }.flatMap {
                it.modify(command, command.newPassword?.let { passwordEncoder.encode(it) })
                memberRepository.save(it)
            }
            .then(Mono.just(Unit))
    }

    fun follow(from: Int, to: Int): Mono<Unit> {
        transactionTemplate.execute {
            if (!memberRepository.existsById(to)) throw MemberNotFoundException()
            if (!memberRepository.existFollowing(from, to))
                followerMappingRepository.save(FollowerMapping(memberNo = from, followerNo = to))
        }

        memberRepository.existsById(to)
            .filter { it }
            .switchIfEmpty(Mono.error(MemberNotFoundException()))
            .flatMap { memberRepository.existsF }
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