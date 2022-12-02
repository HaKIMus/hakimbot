package com.hakim.hakimbot.gamble.command

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.GambleResult
import com.hakim.hakimbot.gamble.Gambler
import com.hakim.hakimbot.gamble.UpsertGambleService
import com.hakim.hakimbot.gamble.exception.InvalidPercentageRangeException
import com.hakim.hakimbot.gamble.exception.NoAmountException
import com.hakim.hakimbot.gamble.exception.NoBalanceException
import com.hakim.hakimbot.gamble.exception.NotSufficientBalanceException
import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.Profile
import com.hakim.hakimbot.network.model.ProfileTable
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.min

class GambleCommand(private val upsertGambleService: UpsertGambleService) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val gambler = upsertGambleService.upsert(event.user)

        when (event.name) {
            "gp", "gamble-percent" -> {
                event.deferReply().queue()

                val percent = event.getOption("percent")?.asInt

                if (percent == null) {
                    event.hook.sendMessage("Podaj procent!").queue()
                    return
                }

                try {
                    val result = gambler.gamble(percent)

                    event.hook.sendMessage(reactToResult(result, event, gambler)).queue()
                } catch (e: NoBalanceException) {
                    event.hook.sendMessage("Nie posiadasz żetonów!").queue()
                } catch (e: NotSufficientBalanceException) {
                    event.hook.sendMessage("Nie posiadasz wystarczająco żetonów!").queue()
                } catch (e: NoAmountException) {
                    event.hook.sendMessage("Musisz podać więcej niż 0 żetonów!").queue()
                } catch (e: InvalidPercentageRangeException) {
                    event.hook.sendMessage("Dozwolony zakres to 1 - 100 procent!").queue()
                }
            }

            "ga", "gamble-amount" -> {
                event.deferReply().queue()
                val amount = event.getOption("amount")?.asDouble

                if (amount == null) {
                    event.hook.sendMessage("Podaj ilość!").queue()
                    return
                }

                try {
                    val result = gambler.gamble(amount)

                    event.hook.sendMessage(reactToResult(result, event, gambler)).queue()
                } catch (e: NoBalanceException) {
                    event.hook.sendMessage("Nie posiadasz żetonów!").queue()
                } catch (e: NotSufficientBalanceException) {
                    event.hook.sendMessage("Nie posiadasz wystarczająco żetonów!").queue()
                } catch (e: NoAmountException) {
                    event.hook.sendMessage("Musisz podać więcej niż 0 żetonów!").queue()
                }
            }

            "gb", "gamble-balance" -> {
                event.deferReply().queue()
                event.hook.sendMessage("Twoje saldo wynosi: ${formatDouble(gambler.balance)} żetonów! :dollar:").queue()
            }

            "gamble-give" -> {
                event.deferReply().queue()
                transaction {
                    val user = event.getOption("user")?.asUser
                    val amount = event.getOption("amount")?.asDouble

                    if (amount == null || user == null) {
                        event.hook.sendMessage("Podaj ilość i użytkownika!").queue()
                        return@transaction
                    }

                    val gamblerToDonate = Gambler.find {
                        GamblerTable.profile eq (Profile.find { ProfileTable.discordUserId eq user.id }.first().id)
                    }.first()

                    gamblerToDonate.balance = (gamblerToDonate.balance + amount)

                    event.hook.sendMessage("Użytkownik dostał żetony!").queue()
                }
            }

            "gtop", "gamble-top" -> {
                event.deferReply().queue()
                transaction {
                    val sorted = Gambler.all().sortedByDescending { it.balance }.take(10)

                    val sb = StringBuilder()

                    sb.append("**Gambling Top 10**\n")

                    for (i in 1..min(sorted.size, 10)) {
                        val currentGambler = sorted[i - 1]

                        val userName = userNameByDiscordId(
                            event.guild!!,
                            currentGambler.profile.discordUserId
                        )
                        sb.append(
                            "$i - **$userName** z wynikiem **${formatDouble(currentGambler.balance)} żetonów** i win/loss streak: **${currentGambler.winStreakMax}**/**${currentGambler.lossStreakMax}**\n"
                        )
                    }

                    event.hook.sendMessage(sb.toString()).queue()
                }
            }
        }
    }

    private fun userNameByDiscordId(guild: Guild, userId: String): String {
        return guild.getMemberById(userId)?.user?.name ?: "Unknown"
    }

    private fun reactToResult(result: GambleResult, event: SlashCommandInteractionEvent, gambler: Gambler): String {
        val hasWon = result.hasWon

        return if (hasWon) {
            """
                <a:BRUHHHH:1000801923273859145> <a:jestGoraco:1000801932081897643> Jasny gwint! ${event.user.name} włożył ${formatDouble(result.investment)} żetonów i wygrał **${
                formatDouble(
                    result.outcome
                )
            } żetonów**! :dollar:
                Win streak: **x${result.streak}** <a:MOOOOOOOOOO:1000802783022305290>
                Nowy stan konta: ${formatDouble(gambler.balance)} żetonów <:peepostonks:971024543621722142>
            """.trimIndent()
        } else {
            """
                <:notLikeThis:801093738353000488> Cholera! ${event.user.name} włożył ${formatDouble(result.investment)} żetonów i przegrał **${
                formatDouble(
                    result.outcome
                )
            } żetonów**! :dollar:
                Loss streak: **x${result.streak}** <a:MOOOOOOOOOO:1000802783022305290> 
                Nowy stan konta: ${formatDouble(gambler.balance)} żetonów <:peeponotstonks:971024543135174657>
            """.trimIndent()
        }
    }
}