package com.hakim.hakimbot.command

import com.hakim.hakimbot.replace
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.random.Random

class IsItTrue : ListenerAdapter() {
    private val falseStatementMessages = arrayOf(
        "Gówno prawda",
    )

    private val trueStatementMessages = arrayOf(
        "Tak to prawda",
        "Jeszcze jak!",
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "czy" -> {
                val optionStatement = event.getOption("statement")?.asString

                if (optionStatement == null) {
                    event.reply("O co chcesz zapytać?").queue()
                    
                    return
                }

                val isFalse = Random.nextBoolean()

                if (isFalse) event.reply(userLyingMessages.random()).queue() else {
                    event.reply(userTellingTruthMessages.random()).queue()
                }
            }
        }
    }
}