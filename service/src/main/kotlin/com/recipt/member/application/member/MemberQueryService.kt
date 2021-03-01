package com.recipt.member.application.member

import com.recipt.core.exception.member.MemberNotFoundException
import com.recipt.member.application.member.dto.FollowerProfileSummary
import com.recipt.member.application.member.dto.MyProfile
import com.recipt.member.application.member.dto.ProfileSummary
import com.recipt.member.domain.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class MemberQueryService(private val memberRepository: MemberRepository) {

    fun getProfile(memberNo: Int): Mono<ProfileSummary> {
        return Mono.fromCallable {
            memberRepository.findByIdOrNull(memberNo) ?: throw MemberNotFoundException()
        }
            .map { ProfileSummary(it) }
            .subscribeOn(Schedulers.elastic())
    }

    fun getMyProfile(memberNo: Int): Mono<MyProfile> {
        return Mono.fromCallable {
            memberRepository.findByIdOrNull(memberNo) ?: throw MemberNotFoundException()
        }
            .map { MyProfile(it) }
            .subscribeOn(Schedulers.elastic())
    }

    // TODO: Paging 적용하기.
    fun getFollowerProfiles(memberNo: Int): Flux<FollowerProfileSummary> {
        return Mono.fromCallable { memberRepository.findFollowerByNo(memberNo) }
            .flatMapMany { Flux.fromIterable(it) }
            .map { FollowerProfileSummary(it) }
            .subscribeOn(Schedulers.elastic())
    }

    fun checkFollowing(from: Int, to: Int): Mono<Boolean> {
        return Mono.fromCallable { memberRepository.existFollowing(from, to) }
            .subscribeOn(Schedulers.elastic())
    }
}