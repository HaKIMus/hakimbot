package com.hakim.hakimbot.command

import com.hakim.hakimbot.HAKIMPL_GUILD_ID
import com.hakim.hakimbot.network.model.Profile
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CreateProfileForEachUser : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "admin-upsert-profiles" -> {
                event.deferReply().queue()

                event.hook.sendMessage("Profiles creating!").queue()

                var i = 0

                transaction {
                    event.jda.getGuildById(HAKIMPL_GUILD_ID)?.members?.forEach {
                        Profile.new(UUID.randomUUID()) {
                            discordUserId = it.user.id
                        }
                        i++
                    }
                }

                event.hook.editOriginal("x$i Profiles created!").queue()
            }
        }
    }
}