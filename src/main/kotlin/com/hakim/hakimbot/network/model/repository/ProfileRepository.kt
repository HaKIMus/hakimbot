package com.hakim.hakimbot.network.model.repository

import com.hakim.hakimbot.network.model.Profile
import com.hakim.hakimbot.network.model.ProfileTable
import org.jetbrains.exposed.sql.transactions.transaction

class ProfileRepository {
    fun getProfileByDiscordUserId(id: String): Profile {
        return transaction {
            Profile.find { ProfileTable.discordUserId eq id }.first()
        }
    }
}