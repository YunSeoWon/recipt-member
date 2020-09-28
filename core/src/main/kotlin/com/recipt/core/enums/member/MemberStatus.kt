package com.recipt.core.enums.member

import com.recipt.core.enums.ReciptEnum

enum class MemberStatus (
    override val code: Int
): ReciptEnum {
    /** 정상 **/
    ACTIVE(1),

    /** 휴면 **/
    DORMANT(2),

    /** 이용 정지 **/
    BLOCKED(3),

    /** 탈퇴 **/
    WITHDRAWAL(4)
}