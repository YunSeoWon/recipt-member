package com.recipt.member.infrastructure.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScans
import org.springframework.stereotype.Repository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@DataJpaTest
@Sql("classpath:sql/member-test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ComponentScan(
    basePackageClasses = [RepositoryImplBase::class],
    useDefaultFilters = false,
    includeFilters = [ComponentScan.Filter(Repository::class)]
)
class MemberRepositoryTest (
    private val memberRepositoryImpl: MemberRepositoryImpl
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    /**
     * (1, 2),
     * (2, 1),
     * (3, 1),
     * (4, 1);
     */
    @Test
    fun `자신이 팔로우한 회원 리스트 추출`() {
        val memberNo = 1

        val result = memberRepositoryImpl.findFollowerByNo(memberNo)

        logger.info("RESULT SET: $result")
        assertEquals(1, result.size)
    }
}