package com.recipt.member.infrastructure.jpa

import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.lang.IllegalStateException
import kotlin.reflect.KClass

abstract class AbstractReciptRepository(kClass: KClass<*>) : QuerydslRepositorySupport(kClass.java) {
    override fun getQuerydsl(): Querydsl = super.getQuerydsl()?: throw IllegalStateException("Quuerydsl is null")
}