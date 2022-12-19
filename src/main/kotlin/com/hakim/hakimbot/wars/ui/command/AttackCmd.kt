package com.hakim.hakimbot.wars.ui.command

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.wars.domain.Round
import com.hakim.hakimbot.wars.domain.WarResultType
import com.hakim.hakimbot.wars.model.ArmyModel
import com.hakim.hakimbot.wars.model.service.TranslateModelToDomain
import com.hakim.hakimbot.wars.service.WarRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import kotlin.math.max
import kotlin.random.Random

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

                    val drawArmyLoss = Random.nextDouble(5.0, 10.0)
                    val winnerArmyLoss = Random.nextDouble(5.0, 15.0)
                    val loserArmyLoss = Random.nextDouble(10.0, 20.0)

                    transaction {
                        val attackerArmy = warRepository.findGeneralArmies(attackerModel)
                        val defenderArmy = warRepository.findGeneralArmies(defenderModel)

                        when (result.result) {
                            WarResultType.ATTACKER_WON -> {
                                distributeArmyLoss(winnerArmyLoss, attackerArmy)
                                distributeArmyLoss(loserArmyLoss, defenderArmy)

                                attackerModel.honorPoints += 10
                                defenderModel.honorPoints -= 10
                            }
                            WarResultType.DEFENDER_WON -> {
                                distributeArmyLoss(loserArmyLoss, attackerArmy)
                                distributeArmyLoss(winnerArmyLoss, defenderArmy)

                                attackerModel.honorPoints -= 10
                                defenderModel.honorPoints -= 10
                            }
                            else -> {
                                distributeArmyLoss(drawArmyLoss, attackerArmy)
                                distributeArmyLoss(drawArmyLoss, defenderArmy)
                            }
                        }
                    }

                    result.rounds.rounds.forEachIndexed { i, it ->
                        event.hook.sendMessageEmbeds(createRoundEmbed(it, i + 1)).queue()
                    }

                    val message: () -> String = {
                        when (result.result) {
                            WarResultType.ATTACKER_WON -> {
                                "${event.user.name} wygrał z ${opponent.name}!" +
                                        "\n **Sraty**" +
                                        "\n ${event.user.name} ${formatDouble(winnerArmyLoss)}% armii" +
                                        "\n ${opponent.name} ${formatDouble(loserArmyLoss)}% armii"
                            }
                            WarResultType.DRAW -> {
                                "${event.user.name} zremisował z ${opponent.name}!" +
                                        "\n **Sraty**" +
                                        "\n ${event.user.name} ${formatDouble(drawArmyLoss)}% armii" +
                                        "\n ${opponent.name} ${formatDouble(drawArmyLoss)}% armii"
                            }
                            else -> {
                                "${event.user.name} przegrał z ${opponent.name}!" +
                                        "\n **Sraty**" +
                                        "\n ${event.user.name} ${formatDouble(loserArmyLoss)}% armii" +
                                        "\n ${opponent.name} ${formatDouble(winnerArmyLoss)}% armii"
                            }
                        }
                    }

                    event.hook.sendMessage(message()).queue()
                }
            }
        }
    }

    private fun distributeArmyLoss(loss: Double, list: List<ArmyModel>) {
        transaction {
            var totalLoss = loss

            for ((i, item) in list.filter { it.amount != 0 }.map { it.amount }.withIndex()) {
                val newValue = max(0.0, item - (totalLoss / list.size))
                list[i].amount = newValue.toInt()
                totalLoss += (item - newValue)
            }
        }
    }

    private fun createRoundEmbed(round: Round, index: Int): MessageEmbed {
        val healthVisualization =
            "Życie atakującego: [${round.attackerHealth}] Życie broniącego się: [${round.opponentHealth}]"

        val embedBuilder = EmbedBuilder()
            .setTitle("Runda #$index") // Set the title of the embedded message
            .addField("Obrażenia atakującego", formatDouble(round.attackerDamage), true) // Add a field for the attacker's damage
            .addField("Obrażenia broniącego się", formatDouble(round.opponentDamage), true) // Add a field for the opponent's damage
            .addField("Health", healthVisualization, false) // Add a field for the health visualization

        embedBuilder.setColor(Color.RED)

        return embedBuilder.build()
    }
}