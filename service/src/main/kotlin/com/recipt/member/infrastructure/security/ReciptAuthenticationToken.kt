package com.recipt.member.infrastructure.security

import com.recipt.core.model.MemberInfo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ReciptAuthenticationToken(
    val memberInfo: MemberInfo,
    authorities: Collection<GrantedAuthority>
) : UsernamePasswordAuthenticationToken(memberInfo.email, null, authorities)