package com.hakim.hakimbot.wars.ui.command

import com.hakim.hakimbot.wars.model.GeneralModel
import com.hakim.hakimbot.wars.model.service.TranslateModelToDomain
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AttackCmd(private val translateModelToDomain: TranslateModelToDomain) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "atak" -> {
                transaction {
                    event.deferReply().queue()

                    val opponent = event.getOption("user")?.asUser!!

                    val attackerModel = GeneralModel.findById(UUID.fromString("5256e82d-6685-4a6c-b62e-a8ce2a70cec9"))!!
                    val defenderModel = GeneralModel.findById(UUID.fromString("5256e82d-6685-4a6c-b62e-a8ce2a70cec9"))!!

                    val attacker = translateModelToDomain.translateGeneralModelToDomain(attackerModel)
                    val defender = translateModelToDomain.translateGeneralModelToDomain(defenderModel)

                    val result = attacker.attack(defender)

                    result.rounds.rounds.forEach {
                        event.hook.sendMessage(it.toString()).queue()
                    }

                    event.hook.sendMessage("Result: ${result.result}").queue()
                }
            }
        }
    }
}