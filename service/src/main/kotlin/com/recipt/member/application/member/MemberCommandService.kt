package com.recipt.member.application.member

import com.recipt.core.exception.member.DuplicatedMemberException
import com.recipt.core.exception.member.MemberNotFoundException
import com.recipt.core.exception.member.WrongPasswordException
import com.recipt.core.extensions.filterOrError
import com.recipt.member.application.member.dto.ProfileModifyCommand
import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.member.entity.FollowerMapping
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.repository.FollowerMappingRepository
import com.recipt.member.domain.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class MemberCommandService(
    private val memberRepository: MemberRepository,
    private val followerMappingRepository: FollowerMappingRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun signUp(command: SignUpCommand): Mono<Unit> {
        return Mono.fromCallable { memberRepository.findByEmailOrNickname(command.email, command.nickname) }
            .handle<Unit> { member, sink ->
                when {
                    command.email == member?.email -> sink.error(DuplicatedMemberException("email"))
                    command.nickname == member?.nickname -> sink.error(DuplicatedMemberException("nickname"))
                    else -> sink.next(Unit)
                }
            }
            .switchIfEmpty {
                memberRepository.save(Member.create(command))
                Mono.just(Unit)
            }
            .subscribeOn(Schedulers.elastic())

    }


    @Transactional
    fun modify(memberNo: Int, command: ProfileModifyCommand): Mono<Unit> {
        return Mono.fromCallable {
            memberRepository.findByIdOrNull(memberNo)
                ?: throw MemberNotFoundException()
        }
            .handle<Member> { member, sink ->
                if (command.newPassword != null && !passwordEncoder.matches(command.password, member.password))
                    sink.error(WrongPasswordException())
                else
                    sink.next(member)
            }.flatMap {
                it.modify(command, command.newPassword?.let { passwordEncoder.encode(it) })
                memberRepository.save(it)
                Mono.just(Unit)
            }
            .subscribeOn(Schedulers.elastic())
    }

    @Transactional
    fun follow(from: Int, to: Int): Mono<Unit> {
        return Mono.fromCallable { memberRepository.existsById(to) }
            .filterOrError(MemberNotFoundException()) { it }
            .map { memberRepository.existFollowing(from, to) }
            .filter { !it }
            .flatMap {
                followerMappingRepository.save(FollowerMapping(memberNo = from, followerNo = to))
                Mono.just(Unit)
            }
    }

    @Transactional
    fun unfollow(from: Int, to: Int): Mono<Unit> {
        return Mono.fromCallable { memberRepository.existsById(to) }
            .filterOrError(MemberNotFoundException()) { it }
            .flatMap {
                followerMappingRepository.findByMemberNoAndFollowerNo(from, to)?.let {
                    followerMappingRepository.deleteById(it.no)
                }
                Mono.just(Unit)
            }
    }
}