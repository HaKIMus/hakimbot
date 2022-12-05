package com.hakim.hakimbot.command

import Product
import com.hakim.hakimbot.gamble.UpsertGambleService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.random.Random

const val LOVE_OR_NOT_COMMAND_PRICE = 10_000.0

class LoveOrNot(private val upsertGambleService: UpsertGambleService) : ListenerAdapter() {
    private val noLoveMessages = arrayOf(
        "Ta suka Cię jebie na kase",
    )

    private val loversMessages = arrayOf(
        "Kocha jak papież kremówki",    
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "love-or-not" -> {
                val gambler = upsertGambleService.upsert(event.user)

                try {
                    gambler.buy(Product(LOVE_OR_NOT_COMMAND_PRICE))
                } catch(e: IllegalArgumentException) {
                    event.reply(e.message!!).queue()
                    return
                }
                
                val optionUser = event.getOption("user")?.asUser

                if (optionUser == null) {
                    event.reply("Musisz podać użytkownika").queue()
                    
                    return
                }

                val isLove = Random.nextBoolean()

                if (isLove) event.reply(noLoveMessages.random()).queue() else {
                    event.reply(loversMessages.random()).queue()
                }
            }
        }
    }
}