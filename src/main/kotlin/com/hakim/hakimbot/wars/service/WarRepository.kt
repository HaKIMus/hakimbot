package com.hakim.hakimbot.wars.service

import com.hakim.hakimbot.gamble.Gambler
import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.repository.ProfileRepository
import com.hakim.hakimbot.wars.domain.General
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

    fun generalBalance(general: General): Double {
        return transaction {
            val profile = profileRepository.getProfileByDiscordUserId(general.profile.discordUserId)

            val gambler = Gambler.find { GamblerTable.profile eq profile.id }.firstOrNull()
                ?: return@transaction 0.0

            return@transaction gambler.balance
        }
    }

    fun generalBalance(general: GeneralModel): Double {
        return transaction {
            val profile = profileRepository.getProfileByDiscordUserId(general.profile.discordUserId)

            val gambler = Gambler.find { GamblerTable.profile eq profile.id }.firstOrNull()
                ?: return@transaction 0.0

            return@transaction gambler.balance
        }
    }

    fun General.balance(): Double {
        return transaction {
            val profile = profileRepository.getProfileByDiscordUserId(profile.discordUserId)

            val gambler = Gambler.find { GamblerTable.profile eq profile.id }.firstOrNull()
                ?: return@transaction 0.0

            return@transaction gambler.balance
        }
    }

    /**
     * Returns reducted balance
     */
    fun reduceBalance(general: GeneralModel, amount: Double): Double {
        return transaction {
            val profile = profileRepository.getProfileByDiscordUserId(general.profile.discordUserId)

            val gambler = Gambler.find { GamblerTable.profile eq profile.id }.firstOrNull()
                ?: return@transaction 0.0

            gambler.balance -= amount

            return@transaction gambler.balance
        }
    }
}