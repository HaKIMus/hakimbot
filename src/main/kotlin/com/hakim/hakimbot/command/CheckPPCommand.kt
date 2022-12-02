package com.hakim.hakimbot.command

import com.hakim.hakimbot.RANDOM_CHANNEL_ID
import com.hakim.hakimbot.formatDouble
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.random.Random

class CheckPPCommand : ListenerAdapter() {
    /**
     * Map of user id and his score
     */
    private val requests = mutableMapOf<String, Double>()

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "pp" -> {
                val randomChannel = event.jda.getTextChannelById(RANDOM_CHANNEL_ID) ?: return

                if (requests.containsKey(event.user.id)) {
                    event.reply("Sprawdź swój wynik na #random!").setEphemeral(true).queue()
                    randomChannel.sendMessage("${event.user.name} próbował/a oszukiwać, ale każdy wie, że jego/jej wynik to ${formatDouble(requests[event.user.id]!!)}cm! <a:yodaSiurpie:1040391656161169478>").queue()

                    return
                }

                val ppSize = Random.nextDouble(0.0, 25.0)

                randomChannel.sendMessage("Niech się przyjrzę! :eyes:").queue()
                randomChannel.sendMessage(mapSizeToMessage(ppSize, event.user.name)).queue()

                requests[event.user.id] = ppSize

                event.reply("Sprawdź swój wynik na #random!").setEphemeral(true).queue()
            }
        }
    }

    private fun mapSizeToMessage(ppSize: Double, authorName: String): String {
        val roundedPpSize = formatDouble(ppSize)

        return when (true) {
            (1.0 > ppSize) -> {
                "<a:BRUHHHH:1000801923273859145> Wyczuwam energię **SMALL PP**! $authorName masz **${
                    formatDouble(ppSize.times(10))
                } milimetrów**!"
            }

            (5.0 > ppSize) -> {
                "<a:lol:1021911748242047077> Mogło być gorzej, $authorName masz **${roundedPpSize} centymetrów**!"
            }

            (10.0 > ppSize) -> {
                "<:PepeShock:968597123463520287> No ok, $authorName masz **${roundedPpSize} centymetrów**!"
            }

            (15.0 > ppSize) -> {
                "<:Pepeogor:968597875800035338> Niezła parówa wariacie, szczasz taką? $authorName masz **${roundedPpSize} centymetrów**!"
            }

            (20.0 > ppSize) -> {
                "<a:boobaRespectfully:986606384940453938> Ja cię chwyco! $authorName masz **${roundedPpSize} centymetrów**!"
            }

            (25.0 > ppSize) -> {
                "<:pepeuwu:968597985401385010> UWU! $authorName masz **${roundedPpSize} centymetrów**!"
            }

            else -> {
                "Łooooooooooooooł! $authorName masz chuj wie ile centymetrów!"
            }
        }
    }
}