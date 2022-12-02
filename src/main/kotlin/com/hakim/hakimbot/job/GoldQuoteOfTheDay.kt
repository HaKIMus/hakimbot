package com.hakim.hakimbot.job

import com.hakim.hakimbot.GOLD_QUOTES_CHANNEL_ID
import com.hakim.hakimbot.RANDOM_CHANNEL_ID
import com.hakim.hakimbot.UNDER_DEVELOPMENT
import com.hakim.hakimbot.changeChannelOnDevelopment
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.awt.Color
import kotlin.time.Duration.Companion.hours

class GoldQuoteOfTheDay(private val jda: JDA) {
    suspend fun execute() {
        if (UNDER_DEVELOPMENT) { return }
        while (true) {
            val quoteOfTheWeek = findRandomGoldQuote() ?: return
            val randomChannel =
                changeChannelOnDevelopment(jda, jda.getTextChannelById(RANDOM_CHANNEL_ID)) as TextChannel?
                    ?: return

            randomChannel.sendMessageEmbeds(embedFromMessage(quoteOfTheWeek)).queue()
            randomChannel.manager.setTopic(("Cytat dnia: " + quoteOfTheWeek.contentRaw).take(1024)).queue()
            delay(24.hours)
        }
    }

    private fun findRandomGoldQuote(): Message? {
        val goldQuotesChannel = jda.getTextChannelById(GOLD_QUOTES_CHANNEL_ID) ?: return null

        return MessageHistory.getHistoryFromBeginning(goldQuotesChannel).complete().retrievedHistory.random()
    }

    private fun embedFromMessage(message: Message): MessageEmbed {
        val builder = EmbedBuilder()

        builder.setTitle("Złoty cytat na dziś!")
        builder.setColor(Color.ORANGE)
        builder.addField("Od", message.author.name, false)
        builder.setDescription(message.contentDisplay)

        return builder.build()
    }
}