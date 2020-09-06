package com.recipt.member.domain.member.entity

import com.recipt.member.domain.converter.MemberStatusConverter
import com.recipt.member.domain.member.enum.MemberStatus
import javax.persistence.*

@Table(name = "MEMBER")
@Entity
data class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    val no: Int = 0,

    @Column(unique = true)
    val email: String,

    @Column(unique = true)
    val nickname: String,

    val password: String,

    val introduction: String = "",

    val mobileNo: String,

    val followerCount: Int = 0,

    @Convert(converter = MemberStatusConverter::class)
    val memberStatus: MemberStatus = MemberStatus.ACTIVE
)