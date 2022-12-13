package com.hakim.hakimbot.wars.ui.command

import com.hakim.hakimbot.wars.domain.WarResultType
import com.hakim.hakimbot.wars.model.service.TranslateModelToDomain
import com.hakim.hakimbot.wars.service.WarRepository
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction

class AttackCmd(
    private val translateModelToDomain: TranslateModelToDomain,
    private val warRepository: WarRepository,
) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "atak" -> {
                transaction {
                    event.deferReply().queue()

                    val opponent = event.getOption("user")?.asUser!!

                    val attackerModel = warRepository.findGeneralByDiscordUser(event.user)
                    val defenderModel = warRepository.findGeneralByDiscordUser(opponent)

                    val attacker = translateModelToDomain.translateGeneralModelToDomain(attackerModel)
                    val defender = translateModelToDomain.translateGeneralModelToDomain(defenderModel)

                    val result = attacker.attack(defender)

                    if (result.result == WarResultType.ATTACKER_WON) {
                        attackerModel.honorPoints += 10
                    } else if (result.result == WarResultType.DEFENDER_WON) {
                        attackerModel.honorPoints -= 10
                    }

                    result.rounds.rounds.forEach {
                        event.hook.sendMessage(it.toString()).queue()
                    }

                    event.hook.sendMessage("Result: ${result.result}").queue()
                }
            }
        }
    }
}