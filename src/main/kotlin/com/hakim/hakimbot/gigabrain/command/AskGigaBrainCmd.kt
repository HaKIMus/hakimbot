package com.hakim.hakimbot.gigabrain.command

import com.hakim.hakimbot.gigabrain.GPTFacade
import com.hakim.hakimbot.gigabrain.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class AskGigaBrainCmd(private val gptFacade: GPTFacade) : ListenerAdapter() {
    private val privilegedUsers = listOf(
        "398811037534781440", // MaciegId
        "233633536509345793", // HaKIM
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) = runBlocking<Unit> {
        when (event.name) {
            "gigabrain" -> {
                event.deferReply().queue()

                if (!privilegedUsers.contains(event.user.id)) {
                    event.hook.sendMessage("Nie masz dostępu do wielkiego mózgu!").queue()

                    return@runBlocking
                }

                val question = event.getOption("question")?.asString

                if (question == null) {
                    event.hook.sendMessage("Nie podałeś pytania!").queue()
                    return@runBlocking
                }

                val result = async(Dispatchers.IO) {
                    gptFacade.askGPT(Question(question))
                }

                event.hook.sendMessage(result.await()).queue()
            }
        }
    }
}