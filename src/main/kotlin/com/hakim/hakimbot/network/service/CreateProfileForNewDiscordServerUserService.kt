package com.hakim.hakimbot.network.service

import com.hakim.hakimbot.network.model.Profile
import com.hakim.hakimbot.wars.service.CreateGeneralFromDiscordUser
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.UUID

class CreateProfileForNewDiscordServerUserService : ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        Profile.new(UUID.randomUUID()) {
            discordUserId = event.user.id
        }
    }
}