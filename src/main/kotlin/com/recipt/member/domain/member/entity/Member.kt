package com.recipt.member.domain.member.entity

import com.recipt.member.application.member.dto.ProfileModifyCommand
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

    @Column(name = "follower_count")
    val followerCount: Int = 0,

    @Column(name = "member_status")
    @Convert(converter = MemberStatusConverter::class)
    val memberStatus: MemberStatus = MemberStatus.ACTIVE
) {
    @Column(name = "name")
    var nickname: String = ""
        private set

    var password: String = ""
        private set

    var introduction: String = ""
        private set

    @Column(name = "mobile_no")
    var mobileNo: String = ""
        private set

    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null
       private set

    companion object {
        fun create(command: SignUpCommand) = Member(
            email = command.email
        ).apply {
            nickname = command.nickname
            password = command.password
            mobileNo = command.mobileNo
        }
    }

    fun modify(command: ProfileModifyCommand, newEncodedPassword: String?) {
        command.introduction?.let { introduction = it }
        command.mobileNo?.let { mobileNo = it }
        newEncodedPassword?.let { password = it }
        command.nickname?.let { nickname = it }
        command.profileImageUrl?.let { profileImageUrl = it }
    }
}