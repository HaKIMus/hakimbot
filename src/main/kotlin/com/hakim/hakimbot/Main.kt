package com.hakim.hakimbot

import com.hakim.hakimbot.command.LOVE_OR_NOT_COMMAND_PRICE
import com.hakim.hakimbot.infrastructure.Dependencies
import com.hakim.hakimbot.infrastructure.ExposedUtilities
import com.hakim.hakimbot.infrastructure.LISTENER_TAG
import com.hakim.hakimbot.job.DailyCoins
import com.hakim.hakimbot.job.GoldQuoteOfTheDay
import com.hakim.hakimbot.wars.domain.unit.UnitType
import com.hakim.hakimbot.wars.model.UnitModel
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
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.allInstances
import org.kodein.di.instance
import java.text.DecimalFormat
import java.util.*
import kotlin.random.Random

const val RANDOM_CHANNEL_ID = "968482612031135806"
const val GOLD_QUOTES_CHANNEL_ID = "968619009786392606"
const val ADMIN_DEVELOPERS_CHANNEL_ID = "1047882371573239901"
const val HAKIMPL_GUILD_ID = "968482610932236298"
const val UNDER_DEVELOPMENT = true

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
                .setActivity(Activity.competing("WPIERDZIELAM PIERO??KI!"))
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
                Commands.slash("zjedz-piero??ka", "Masno"),
                Commands
                    .slash("twitter", "??wierk")
                    .addOption(OptionType.INTEGER, "twitter_id", "User twitter id", true)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands
                    .slash("maja", "??wierka maja")
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands.slash("pp", "Sprwad?? par??w?? wariacie!"),
                Commands
                    .slash("wrozbita-maciej", "Odkryj prawd??!")
                    .addOption(OptionType.USER, "user", "Sprawd?? czy on/a k??amie!", true),
                Commands.slash("gamble-balance", "Zobacz sw??j stan konta!"),
                Commands.slash("gb", "Zobacz sw??j stan konta!"),
                Commands.slash("gtop", "Zobacz Gambling Top!"),
                Commands.slash("gamble-top", "Zobacz Gambling Top!"),
                Commands
                    .slash("gamble-percent", "Ponie?? si?? losu i podw??j swoje ??etony!")
                    .addOption(OptionType.INTEGER, "percent", "% Twojego salda!", true),
                Commands
                    .slash("gp", "Ponie?? si?? losu i podw??j swoje ??etony!")
                    .addOption(OptionType.INTEGER, "percent", "% Twojego salda!", true),
                Commands
                    .slash("gamble-amount", "Ponie?? si?? losu i wygraj pieni??dze!")
                    .addOption(OptionType.NUMBER, "amount", "Konkretna liczba dziesi??tna", true),
                Commands
                    .slash("ga", "Ponie?? si?? losu i wygraj pieni??dze!")
                    .addOption(OptionType.NUMBER, "amount", "Konkretna liczba dziesi??tna", true),
                Commands.slash("gamble-give", "Daj u??ytkownikowi ??etony")
                    .addOption(OptionType.USER, "user", "U??ytkownik, kt??ry ma dosta?? ??etony", true)
                    .addOption(OptionType.NUMBER, "amount", "Ile ??eton??w", true)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands.slash("gpozycz", "Po??ycz u??ytkownikowi ??etony")
                    .addOption(OptionType.USER, "user", "U??ytkownik, kt??ry ma dosta?? ??etony", true)
                    .addOption(OptionType.NUMBER, "amount", "Ile ??eton??w", true),
                Commands.slash("gamble-pozycz", "Po??ycz u??ytkownikowi ??etony")
                    .addOption(OptionType.USER, "user", "U??ytkownik, kt??ry ma dosta?? ??etony", true)
                    .addOption(OptionType.NUMBER, "amount", "Ile ??eton??w", true),
                Commands.slash("czy", "Sprawd?? czy to prawda")
                    .addOption(OptionType.STRING, "statement", "Co chcesz sprawdzi??", true),
                Commands.slash("admin-upsert-profiles", "Create profile for each user")
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands.slash("love-or-not", "Sprawd?? czy kocha | koszt: $LOVE_OR_NOT_COMMAND_PRICE")
                    .addOption(OptionType.USER, "user", "U??ytkownik do sprawdzenia", true),
                Commands
                    .slash("atak", "Zaatakuj innego gracza")
                    .addOption(OptionType.USER, "user", "Kogo chcesz zaatakowa??", true),
                Commands
                    .slash("awar-upsert-generals", "Creates generals for users")
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED),
                Commands.slash("kup-armie", "Kup jednostki do swojej armii")
                    .addOption(OptionType.STRING, "unit", "Typ jednostki")
                    .addOption(OptionType.INTEGER, "amount", "Ilo????"),
                Commands.slash("gigabrain", "Zadaj pytanie gigabrainowi")
                    .addOption(OptionType.STRING, "question", "Tre???? pytania")
            ).queue()
        }

        private fun testWar() {
            transaction {
                UnitModel.new(UUID.randomUUID()) {
                    type = UnitType.MELEE.name
                    name = "Wojownik"
                    healthPoints = 12
                    meleeProtection = 30
                    rangeProtection = 15
                    damageMin = 5.0
                    damageMax = 10.0
                }
            }
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

fun randomize(chance: Double): Boolean {
    return Random.nextDouble(0.01, 100.0) < chance
}