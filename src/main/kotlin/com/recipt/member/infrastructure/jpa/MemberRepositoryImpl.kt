package com.recipt.member.infrastructure.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.recipt.member.domain.member.entity.Member
import com.recipt.member.domain.member.entity.QFollowerMapping
import com.recipt.member.domain.member.entity.QMember
import com.recipt.member.domain.member.repository.MemberAggregateRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import javax.annotation.Resource

@Repository
class MemberRepositoryImpl : MemberAggregateRepository, QuerydslRepositorySupport(Member::class.java) {

    private val member = QMember.member

    private val followerMapping = QFollowerMapping.followerMapping

    override fun findFollowerByNo(no: Int): List<Member> {
        return from(member)
            .leftJoin(followerMapping)
            .on(followerMapping.followerNo.eq(member.no))
            .where(followerMapping.memberNo.eq(no))
            .fetch()
    }
}