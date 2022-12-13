package com.hakim.hakimbot.wars.service

import com.hakim.hakimbot.network.model.repository.ProfileRepository
import com.hakim.hakimbot.wars.model.ArmyModel
import com.hakim.hakimbot.wars.model.GeneralModel
import com.hakim.hakimbot.wars.model.table.ArmyTable
import com.hakim.hakimbot.wars.model.table.GeneralTable
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.transactions.transaction

class WarRepository(private val profileRepository: ProfileRepository) {
    fun findGeneralByDiscordUser(user: User): GeneralModel {
        return transaction {
            GeneralModel.find {
                GeneralTable.profile eq profileRepository.getProfileByDiscordUserId(user.id).id
            }.first()
        }
    }

    fun findGeneralArmies(general: GeneralModel): List<ArmyModel> {
        return transaction {
            ArmyModel.find { ArmyTable.general eq general.id }.toList()
        }
    }
}