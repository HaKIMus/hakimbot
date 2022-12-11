package com.hakim.hakimbot.wars.model

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
import com.hakim.hakimbot.wars.model.table.ArmyTable
import com.hakim.hakimbot.wars.model.table.GeneralTable
import com.hakim.hakimbot.wars.model.table.WarTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class WarModel(id: EntityID<UUID>) : BaseUuidEntity(id, WarTable) {
    companion object : BaseUuidEntityClass<WarModel>(WarTable)

    val uuid = id.value
}