package com.recipt.member.infrastructure.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

@Sql("classpath:sql/member-test.sql")
class MemberRepositoryTest(
    @Autowired private val memberRepositoryImpl: MemberRepositoryImpl
) : ReciptJpaTest() {

    /** 팔로우 관계
     * (1 -> 2),
     * (2 -> 1),
     * (3 -> 1),
     * (4 -> 1);
     */
    @Test
    fun `자신이 팔로우한 회원 리스트 추출`() {
        val memberNo = 1

        val result = memberRepositoryImpl.findFollowerByNo(memberNo)

        assertEquals(1, result.size)
    }

    @Test
    fun `팔로우 되어있는지 확인`() {
        val memberNo = 1

        val expectedOpponentFollowRelation = mapOf(
            2 to true,
            3 to false,
            4 to false
        )

        expectedOpponentFollowRelation.forEach { oppo, result ->
            assertEquals(result, memberRepositoryImpl.existFollowing(from = memberNo, to = oppo))
        }
    }
}