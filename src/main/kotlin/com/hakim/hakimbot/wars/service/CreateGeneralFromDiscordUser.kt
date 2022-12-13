package com.hakim.hakimbot.wars.service

import com.hakim.hakimbot.network.model.repository.ProfileRepository
import com.hakim.hakimbot.wars.SOLDIER_ID
import com.hakim.hakimbot.wars.model.ArmyModel
import com.hakim.hakimbot.wars.model.GeneralModel
import com.hakim.hakimbot.wars.model.UnitModel
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CreateGeneralFromDiscordUser(private val profileRepository: ProfileRepository) {
    fun create(user: User) {
        transaction {
            val general = GeneralModel.new(UUID.randomUUID()) {
                profile = profileRepository.getProfileByDiscordUserId(user.id)
                honorPoints = 100
            }

            ArmyModel.new(UUID.randomUUID()) {
                this.unit = UnitModel.findById(UUID.fromString(SOLDIER_ID))!!
                this.general = general
                this.amount = 100
            }
        }
    }
}