package com.recipt.member.domain.converter

import com.recipt.core.enums.member.MemberStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class MemberStatusConverter: AttributeConverter<MemberStatus, Int> {
    override fun convertToDatabaseColumn(attribute: MemberStatus): Int {
        return attribute.code
    }

    override fun convertToEntityAttribute(dbData: Int): MemberStatus {
        return MemberStatus.values().find { it.code == dbData } ?: throw Exception() // TODO: ConvertException 정
    }
}