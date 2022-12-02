package com.hakim.hakimbot.listener

import com.hakim.hakimbot.replace
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class TagMeListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val mentions = event.message.mentions.getMentions(Message.MentionType.USER)

        if (mentions.isEmpty()) {
            return
        }

        val isHakimMentioned = mentions.firstOrNull {
            it as Member
            it.id == "233633536509345793"
        } != null

        if (isHakimMentioned) {
            event.channel.sendMessage(HAPPY_MESSAGE.replace(mapOf("%user%" to event.author.name))).queue()
        }
    }
}

const val HAPPY_MESSAGE = """
    %user% no siema ruro. Przestałem jeść pierożki, żeby Ci napisać, że wiem gdzie mieszkasz jebany śmieszku.
"""