package com.recipt.member.presentation.security

import com.recipt.member.presentation.model.MemberInfo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ReciptAuthenticationToken(
    val memberInfo: MemberInfo,
    authorities: Collection<GrantedAuthority>
) : UsernamePasswordAuthenticationToken(memberInfo.email, null, authorities)