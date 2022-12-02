package com.hakim.hakimbot.network.model

import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UpsertProfileService {
    fun upsert(user: User) {
        transaction {
            if (Profile.find { ProfileTable.discordUserId eq user.id }.firstOrNull() == null) {
                Profile.new(UUID.randomUUID()) {
                    discordUserId = user.id
                }
            }
        }
    }
}