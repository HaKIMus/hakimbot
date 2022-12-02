package com.hakim.hakimbot.gamble

import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.Profile
import com.hakim.hakimbot.network.model.ProfileTable
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UpsertGambleService {
    fun upsert(user: User): Gambler {
        return transaction {
            val profile = Profile.find { ProfileTable.discordUserId eq user.id }.first()
            val gambler = Gambler.find {
                GamblerTable.profile eq (Profile.find { ProfileTable.discordUserId eq user.id }.first().id)
            }.firstOrNull()

            if (gambler == null) {
                return@transaction Gambler.new(UUID.randomUUID()) {
                    this.profile = profile
                    this.balance = 1000.0
                }
            } else {
                return@transaction gambler
            }
        }
    }
}