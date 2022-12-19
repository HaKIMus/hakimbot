package com.hakim.hakimbot.wars.ui.command

import com.hakim.hakimbot.HAKIMPL_GUILD_ID
import com.hakim.hakimbot.wars.service.CreateGeneralFromDiscordUser
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction

class CreateGeneralForEachUser(
    private val createGeneralFromDiscordUser: CreateGeneralFromDiscordUser,
) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "awar-upsert-generals" -> {
                event.deferReply().queue()

                event.hook.sendMessage("Profiles creating!").queue()

                var i = 0

                transaction {
                    event.jda.getGuildById(HAKIMPL_GUILD_ID)?.members?.forEach {
                        createGeneralFromDiscordUser.create(it.user)
                        i++
                    }
                }

                event.hook.editOriginal("x$i Profiles created!").queue()
            }
        }
    }
}