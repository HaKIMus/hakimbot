package com.hakim.hakimbot.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class DumplingCommand : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "ping" -> {
                val time = System.currentTimeMillis()
                event.reply("Pong!")
                    .setEphemeral(true)
                    .flatMap {
                        event.hook.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
                    }
                    .queue()
            }
            "zjedz-pierożka" -> {
                event.deferReply(true).queue()
                event.hook
                    .sendMessage("Ja pierdole! Jakie dobre!")
                    .queue()
            }
        }
    }
}