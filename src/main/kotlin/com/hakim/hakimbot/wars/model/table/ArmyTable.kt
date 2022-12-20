package com.hakim.hakimbot.wars.model.table

import com.hakim.hakimbot.common.exposed.BaseUUIDTable
import com.hakim.hakimbot.wars.domain.General

object ArmyTable : BaseUUIDTable("wars_armies") {
    val amount = integer("amount")
    val general = reference("general", GeneralTable)
    val unit = reference("unit", UnitTable)
}