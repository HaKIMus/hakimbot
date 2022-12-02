package com.hakim.hakimbot.network.model

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Profile(id: EntityID<UUID>) : BaseUuidEntity(id, ProfileTable) {
    companion object: BaseUuidEntityClass<Profile>(ProfileTable)

    val uuid = id.value
    var discordUserId by ProfileTable.discordUserId
}