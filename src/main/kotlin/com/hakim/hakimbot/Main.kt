package com.hakim.hakimbot

import com.hakim.hakimbot.infrastructure.Dependencies
import com.hakim.hakimbot.infrastructure.ExposedUtilities
import com.hakim.hakimbot.infrastructure.LISTENER_TAG
import com.hakim.hakimbot.job.DailyCoins
import com.hakim.hakimbot.job.GoldQuoteOfTheDay
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.kodein.di.DI
import org.kodein.di.allInstances
import org.kodein.di.instance
import java.text.DecimalFormat

const val RANDOM_CHANNEL_ID = "968482612031135806"
const val GOLD_QUOTES_CHANNEL_ID = "968619009786392606"
const val ADMIN_DEVELOPERS_CHANNEL_ID = "1047882371573239901"
const val UNDER_DEVELOPMENT = false

class Main {
    companion object {
        private lateinit var container: DI
        @OptIn(DelicateCoroutinesApi::class)
        private val jobsThread = newFixedThreadPoolContext(2, "jobs")

        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            container = Dependencies(args).dependencies

            val exposedUtilities: ExposedUtilities by container.instance()
            exposedUtilities.createMissingTablesAndColumns()

            val listeners by container.allInstances<ListenerAdapter>(LISTENER_TAG)
            val discordData: DiscordData by container.instance()

            val builder = JDABuilder.createDefault(
                discordData.token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
            )
                .setActivity(Activity.competing("WPIERDZIELAM PIEROŻKI!"))
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)

            listeners.forEach {
                builder.addEventListeners(it)
            }

            val jda = builder.build().awaitReady()
            jda.getGuildById(968482610932236298)!!.loadMembers()

            val goldQuoteOfTheDayJob = GoldQuoteOfTheDay(jda)
            val dailyCoinsJob = DailyCoins()
            launch(jobsThread) {
                launch { goldQuoteOfTheDayJob.execute() }
                launch { dailyCoinsJob.execute() }
            }

            jda.updateCommands().addCommands(
                Commands.slash("ping", "Oblicz ping tego bota"),
                Commands.slash("zjedz-pierożka", "Masno"),
                Commands
                    .slash("twitter", "Ćwierk")
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands
                    .slash("maja", "Ćwierka maja")
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands.slash("pp", "Sprwadź parówę wariacie!"),
                Commands
                    .slash("wrozbita-maciej", "Odkryj prawdę!")
                    .addOption(OptionType.USER, "user", "Sprawdź czy on/a kłamie!", true),
                Commands.slash("gamble-balance", "Zobacz swój stan konta!"),
                Commands.slash("gb", "Zobacz swój stan konta!"),
                Commands.slash("gtop", "Zobacz Gambling Top!"),
                Commands.slash("gamble-top", "Zobacz Gambling Top!"),
                Commands
                    .slash("gamble-percent", "Ponieś się losu i podwój swoje żetony!")
                    .addOption(OptionType.INTEGER, "percent", "% Twojego salda!", true),
                Commands
                    .slash("gp", "Ponieś się losu i podwój swoje żetony!")
                    .addOption(OptionType.INTEGER, "percent", "% Twojego salda!", true),
                Commands
                    .slash("gamble-amount", "Ponieś się losu i wygraj pieniądze!")
                    .addOption(OptionType.NUMBER, "amount", "Konkretna liczba dziesiętna", true),
                Commands
                    .slash("ga", "Ponieś się losu i wygraj pieniądze!")
                    .addOption(OptionType.NUMBER, "amount", "Konkretna liczba dziesiętna", true),
                Commands.slash("gamble-give", "Daj użytkownikowi żetony")
                    .addOption(OptionType.USER, "user", "Użytkownik, który ma dostać żetony", true)
                    .addOption(OptionType.NUMBER, "amount", "Ile żetonów", true)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands.slash("gpozycz", "Pożycz użytkownikowi żetony")
                    .addOption(OptionType.USER, "user", "Użytkownik, który ma dostać żetony", true)
                    .addOption(OptionType.NUMBER, "amount", "Ile żetonów", true),
                Commands.slash("gamble-pozycz", "Pożycz użytkownikowi żetony")
                    .addOption(OptionType.USER, "user", "Użytkownik, który ma dostać żetony", true)
                    .addOption(OptionType.NUMBER, "amount", "Ile żetonów", true),
                ).queue()
        }
    }
}

fun String.replace(mapping: Map<String, String?>): String {
    var str = this
    mapping.forEach { str = str.replace(it.key, it.value ?: "") }
    return str
}

fun <T : Channel?> changeChannelOnDevelopment(jda: JDA, channel: T): Channel? {
    if (UNDER_DEVELOPMENT) {
        return jda.getTextChannelById(ADMIN_DEVELOPERS_CHANNEL_ID)
    }

    return channel
}

fun formatDouble(double: Double): String {
    val decimalFormat = DecimalFormat("#.##")
    return decimalFormat.format(double)
}