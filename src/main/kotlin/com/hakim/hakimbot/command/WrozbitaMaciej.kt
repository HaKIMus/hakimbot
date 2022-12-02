package com.hakim.hakimbot.command

import com.hakim.hakimbot.replace
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.random.Random

class WrozbitaMaciej : ListenerAdapter() {
    private val userLyingMessages = arrayOf(
        "%username% łże teraz jak pies!",
        "Laluna, co ty pierdolisz? %username% kłamie!",
        "%username% :billed_cap:.",
        "%username%, naucz się kłamać!",
        "%username% a ja wpierdalam gołąbki!",
        "%username%, tak? To zajebiście!",
        "%username% jak nie zobaczę to nie uwierzę!",
    )

    private val userTellingTruthMessages = arrayOf(
        "%username% tym razem mówi prawdę.",
        "Nie wyczuwam kłamstwa!",
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "wrozbita-maciej" -> {
                val optionUser = event.getOption("user")?.asUser

                if (optionUser == null) {
                    event.reply("Nie znaleziono tego użytkownika!")
                    
                    return
                }

                val isLying = Random.nextBoolean()

                if (isLying) event.reply(userLyingMessages.random().replace(mapOf("%username%" to optionUser.name))).queue() else {
                    event.reply(userTellingTruthMessages.random().replace(mapOf("%username%" to optionUser.name))).queue()
                }
            }
        }
    }
}