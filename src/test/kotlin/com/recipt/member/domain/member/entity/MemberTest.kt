package com.recipt.member.domain.member.entity

import com.recipt.member.application.member.dto.SignUpCommand
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class MemberTest {

    @Test
    fun `회원 생성`() {
        val command = SignUpCommand(
            "email@example.com",
            "password",
            "nickname",
            "010-1234-5678"
        )

        val member = Member.create(command)

        assertEquals(command.email, member.email)
        assertEquals(command.password, member.password)
        assertEquals(command.nickname, member.nickname)
        assertEquals(command.mobileNo, member.mobileNo)
    }
}