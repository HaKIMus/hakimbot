package com.hakim.hakimbot.wars.ui.command

import com.hakim.hakimbot.wars.model.ArmyModel
import com.hakim.hakimbot.wars.model.UnitModel
import com.hakim.hakimbot.wars.service.WarRepository
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class BuyArmyUnitsCmd(private val warRepository: WarRepository) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        transaction {
            when (event.name) {
                "kup-armie" -> {
                    event.deferReply(true).queue()

                    val purchaseUnit = event.getOption("unit")?.asString
                    val purchaseAmount = event.getOption("amount")?.asInt

                    if (purchaseUnit == null || purchaseAmount == null) {
                        event.hook.sendMessage("Podaj nazwę jednostki i ilość.").queue()

                        return@transaction
                    }

                    val units = UnitModel.all()
                    val unitModelUserWantsToBuy = units.firstOrNull { it.name.equals(purchaseUnit, true) }

                    if (unitModelUserWantsToBuy == null) {
                        event.hook.sendMessage("Dostępne jednostki to:\n ${units.joinToString("\n") { it.name }}").queue()

                        return@transaction
                    }

                    val general = warRepository.findGeneralByDiscordUser(event.user)
                    val armies = warRepository.findGeneralArmies(general)

                    val purchasePrice = unitModelUserWantsToBuy.price * purchaseAmount

                    if (warRepository.generalBalance(general) < purchasePrice) {
                        event.hook.sendMessage("Nie masz wystarczającą ilość żetonów.").queue()
                        event.hook.sendMessage("Wymagana ilość żetonów to: ${purchasePrice}. Cena za jednostkę: ${unitModelUserWantsToBuy.price}")
                            .queue()
                        return@transaction
                    }

                    val armyOfTheUnitUserWantsToBuy = armies.firstOrNull {
                        it.unit.name.equals(purchaseUnit, true)
                    }

                    if (armyOfTheUnitUserWantsToBuy == null) {
                        transaction {
                            ArmyModel.new(UUID.randomUUID()) {
                                this.general = general
                                this.unit = unitModelUserWantsToBuy
                                amount = purchaseAmount
                            }
                        }
                    } else {
                        transaction {
                            armyOfTheUnitUserWantsToBuy.amount += purchaseAmount
                        }
                    }

                    warRepository.reduceBalance(general, purchasePrice)

                    event.hook.sendMessage("Kupiłeś x$purchaseAmount $purchaseUnit za $purchasePrice żetonów!").queue()
                }
            }
        }

    }
}