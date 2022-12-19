package com.hakim.hakimbot.wars.listener

import com.hakim.hakimbot.wars.service.CreateGeneralFromDiscordUser
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CreateGeneralForNewDiscordServerUserService(
    private val createGeneralFromDiscordUser: CreateGeneralFromDiscordUser
) : ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        createGeneralFromDiscordUser.create(event.user)
    }
}