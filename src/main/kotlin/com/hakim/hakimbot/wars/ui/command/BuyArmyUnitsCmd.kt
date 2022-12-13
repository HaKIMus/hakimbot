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
        if (event.name != "kup-armie") {
            return
        }

        event.deferReply(true).queue()

        val purchaseUnit = event.getOption("unit")?.asString!!
        val purchaseAmount = event.getOption("amount")?.asInt!!

        val units = UnitModel.all()
        val unitModelUserWantsToBuy = units.firstOrNull { it.name == purchaseUnit }

        if (unitModelUserWantsToBuy == null) {
            event.hook.sendMessage("Dostępne jednostki to:").queue()
            units.forEach {
                event.hook.sendMessage(it.name).queue()
            }

            return
        }

        val general = warRepository.findGeneralByDiscordUser(event.user)
        val armies = warRepository.findGeneralArmies(general)

        val armyOfTheUnitUserWantsToBuy = armies.firstOrNull {
            it.unit.name == purchaseUnit
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
                armyOfTheUnitUserWantsToBuy.amount =+ purchaseAmount
            }
        }

        event.hook.sendMessage("Kupiłeś x$purchaseAmount $purchaseUnit").queue()
    }
}