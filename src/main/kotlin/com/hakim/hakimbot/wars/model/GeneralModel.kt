package com.hakim.hakimbot.wars.model

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
import com.hakim.hakimbot.gamble.Gambler
import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.Profile
import com.hakim.hakimbot.network.model.Profile.Companion.referrersOn
import com.hakim.hakimbot.wars.model.table.ArmyTable
import com.hakim.hakimbot.wars.model.table.GeneralTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GeneralModel(id: EntityID<UUID>) : BaseUuidEntity(id, GeneralTable) {
    companion object : BaseUuidEntityClass<GeneralModel>(GeneralTable)

    val uuid = id.value
    var profile by Profile referencedOn GeneralTable.profile
    var honorPoints by GeneralTable.honorPoints
}