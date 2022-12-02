package com.hakim.hakimbot.command

import com.hakim.hakimbot.twitter.TwitterFacade
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class TwitterCommand(private val twitterFacade: TwitterFacade) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "twitter" -> {
                val elonMuskTweets = twitterFacade.readUserTweets(44196397L)

                event.reply(elonMuskTweets.last().text + "\n" + elonMuskTweets.first().text).queue()
            }
            "maja" -> {
                val majaStaskoTweets = twitterFacade.readUserTweets(3019814968L)
                val eb = EmbedBuilder().apply {
                    setTitle("Tweet @majakstasko na dziś")
                    setColor(Color(29, 161, 242))
                    setDescription(majaStaskoTweets.random().text)
                    addBlankField(false)
                    setThumbnail("https://pbs.twimg.com/card_img/1596893061736431618/VFtaqrxW?format=jpg&name=small")
                }

                event.replyEmbeds(eb.build()).queue()
            }
        }
    }
}