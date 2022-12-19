package com.hakim.hakimbot.wars.model

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
import com.hakim.hakimbot.wars.domain.General
import com.hakim.hakimbot.wars.model.table.ArmyTable
import com.hakim.hakimbot.wars.model.table.GeneralTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class ArmyModel(id: EntityID<UUID>) : BaseUuidEntity(id, ArmyTable) {
    companion object : BaseUuidEntityClass<ArmyModel>(ArmyTable)

    val uuid = id.value

    var unit by UnitModel referencedOn ArmyTable.unit
    var amount by ArmyTable.amount
    var general by GeneralModel referencedOn ArmyTable.general
}