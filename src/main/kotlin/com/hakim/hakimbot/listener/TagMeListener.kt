package com.hakim.hakimbot.listener

import com.hakim.hakimbot.replace
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class TagMeListener : ListenerAdapter() {
    private val messages = listOf(
        "%user% no siema ruro. Przestałem jeść pierożki, żeby Ci napisać, że wiem gdzie mieszkasz jebany śmieszku.",
        "Wiesz dlaczego papież nie może zjeść burgera?"
    )
    private val privilegedUsers = listOf(
        "709112024705138719", //JanekId
        "565972477717512193", // BartekId
        "726251617099448393", // PepegaId
        "398811037534781440" // MaciegId
    )
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val mentions = event.message.mentions.getMentions(Message.MentionType.USER)

        if (mentions.isEmpty()) {
            return
        }

        if (privilegedUsers.contains(event.author.id)) {
            return
        }

        val isHakimMentioned = mentions.firstOrNull {
            it as Member
            it.id == "233633536509345793"
        } != null

        if (isHakimMentioned) {
            event.channel.sendMessage(messages.random().replace(mapOf("%user%" to event.author.name))).queue()
        }
    }
}
