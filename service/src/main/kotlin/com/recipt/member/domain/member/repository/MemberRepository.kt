package com.recipt.member.domain.member.repository

import com.recipt.member.domain.member.entity.Member
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface MemberRepository : R2dbcRepository<Member, Int>, MemberAggregateRepository {
    fun findFirstByEmailOrNickname(email: String, nickname: String): Mono<Member>
    fun findFirstByEmail(email: String): Mono<Member>
}
