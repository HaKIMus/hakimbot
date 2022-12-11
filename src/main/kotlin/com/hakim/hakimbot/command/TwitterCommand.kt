package com.hakim.hakimbot.command

import com.hakim.hakimbot.twitter.TwitterFacade
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import twitter4j.Tweet
import java.awt.Color

class TwitterCommand(private val twitterFacade: TwitterFacade) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "twitter" -> {
                event.deferReply().queue()

                val twitterId = event.getOption("twitter_id")?.asLong ?: return
                val elonMuskTweets = twitterFacade.readUserTweets(twitterId)

                event.hook.sendMessageEmbeds(buildEbForTweet(elonMuskTweets.first())).queue()
            }
            "maja" -> {
                event.deferReply().queue()

                val majaStaskoTweets = twitterFacade.readUserTweets(3019814968L)

                event.hook
                    .sendMessageEmbeds(
                        buildEbForTweet(
                            majaStaskoTweets.random(),
                            "https://pbs.twimg.com/card_img/1596893061736431618/VFtaqrxW?format=jpg&name=small"
                        )
                    )
                    .queue()
            }
        }
    }

    private fun buildEbForTweet(tweet: Tweet, thumbnailUrl: String? = null): MessageEmbed {
        return EmbedBuilder().apply {
            setTitle("Tweet @${ twitterFacade.tweetAuthorName(tweet.authorId!!) } na dziś")
            setColor(Color(29, 161, 242))
            setDescription(tweet.text)
            addBlankField(false)
            setThumbnail(thumbnailUrl)
        }.build()
    }
}