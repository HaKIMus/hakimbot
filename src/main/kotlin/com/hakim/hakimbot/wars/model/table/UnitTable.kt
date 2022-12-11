package com.hakim.hakimbot.wars.model.table

import com.hakim.hakimbot.common.exposed.BaseUUIDTable
import com.hakim.hakimbot.wars.domain.unit.UnitAttackProtection
import com.hakim.hakimbot.wars.domain.unit.UnitDamageRange
import com.hakim.hakimbot.wars.domain.unit.UnitType
import org.jetbrains.exposed.dao.id.UUIDTable

object UnitTable : BaseUUIDTable("wars_units") {
    val type = varchar("type", 255)
    val name = varchar("name", 255)
    val healthPoints = integer("health_points")
    val meleeProtection = integer("melee_protection")
    val rangeProtection = integer("range_protection")
    val damageMin = double("damage_min")
    val damageMax = double("damage_max")
}