package com.recipt.member.domain.member.entity

import com.recipt.member.application.member.dto.SignUpCommand
import com.recipt.member.domain.converter.MemberStatusConverter
import com.recipt.member.domain.member.enums.MemberStatus
import javax.persistence.*

@Table(name = "RECIPT_MEMBER")
@Entity
data class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    val no: Int = 0,

    val email: String,

    @Column(name = "name")
    val nickname: String,

    val password: String,

    val introduction: String = "",

    @Column(name = "mobile_no")
    val mobileNo: String,

    @Column(name = "follower_count")
    val followerCount: Int = 0,

    @Column(name = "member_status")
    @Convert(converter = MemberStatusConverter::class)
    val memberStatus: MemberStatus = MemberStatus.ACTIVE,

    @Column(name = "profile_image_url")
    val profileImageUrl: String? = null
) {
    companion object {
        fun create(command: SignUpCommand) = Member(
            email = command.email,
            nickname = command.nickname,
            password = command.password,
            mobileNo = command.mobileNo
        )
    }
}