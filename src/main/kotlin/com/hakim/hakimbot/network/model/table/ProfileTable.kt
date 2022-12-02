package com.hakim.hakimbot.network.model

import com.hakim.hakimbot.common.exposed.BaseUUIDTable

object ProfileTable : BaseUUIDTable("profiles") {
    val discordUserId = varchar("discord_user_id", 255)
}