package com.recipt.member.presentation.model.request

import com.recipt.member.application.member.dto.ProfileModifyCommand
import com.recipt.core.constants.ValidationPatterns
import com.recipt.core.constants.checkPatternMatch
import com.recipt.core.exception.request.InvalidParameterException
import javax.validation.constraints.Pattern

data class ProfileModifyRequest (
    val password: String? = null,
    val nickname: String? = null,
    val mobileNo: String? = null,
    val introduction: String? = null,
    val profileImageUrl: String? = null,
    val newPassword: String? = null
) {
    /** nullable한 것은 따로 검사.. **/
    fun validate() {
        mobileNo?.let {
            if(!checkPatternMatch(ValidationPatterns.PHONE, it))
                throw InvalidParameterException("mobileNo")
        }
        password?.let {
            if(!checkPatternMatch(ValidationPatterns.PASSWORD, it))
                throw InvalidParameterException("newPassword")
        }
        newPassword?.let {
            if(!checkPatternMatch(ValidationPatterns.PASSWORD, it))
                throw InvalidParameterException("newPassword")
        }
    }

    fun toCommand() = ProfileModifyCommand(
        password,
        nickname,
        mobileNo,
        introduction,
        profileImageUrl,
        newPassword
    )
}