package com.recipt.member.domain.member.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

// @see https://deep-dive-dev.tistory.com/38
@Table(name = "FOLLOWER_MAPPING")
@Entity
data class FollowerMapping(
    @Id
    @Column(name = "follower_mapping_no")
    val no: Int,

    @Column(name = "member_no")
    val memberNo: Int,

    @Column(name = "follower_no")
    val followerNo: Int
)