package com.hakim.hakimbot.listener

import com.hakim.hakimbot.infrastructure.HakimBotCoroutineScope
import com.hakim.hakimbot.network.model.UpsertProfileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CreateProfileForUserListener(private val upsertProfileService: UpsertProfileService) : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        CoroutineScope(HakimBotCoroutineScope().coroutineContext).launch {
            upsertProfileService.upsert(event.author)
        }
    }
}