package com.hakim.hakimbot.wars.model

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
import com.hakim.hakimbot.wars.domain.unit.UnitType
import com.hakim.hakimbot.wars.model.table.ArmyTable
import com.hakim.hakimbot.wars.model.table.GeneralTable
import com.hakim.hakimbot.wars.model.table.UnitTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UnitModel(id: EntityID<UUID>) : BaseUuidEntity(id, UnitTable) {
    companion object : BaseUuidEntityClass<UnitModel>(UnitTable)

    val uuid = id.value
    var type by UnitTable.type
    var name by UnitTable.name
    var healthPoints by UnitTable.healthPoints
    var meleeProtection by UnitTable.meleeProtection
    var rangeProtection by UnitTable.rangeProtection
    var damageMin by UnitTable.damageMin
    var damageMax by UnitTable.damageMax
}