package com.hakim.hakimbot.infrastructure

import com.hakim.hakimbot.DiscordData
import com.hakim.hakimbot.command.*
import com.hakim.hakimbot.gamble.UpsertGambleService
import com.hakim.hakimbot.gamble.command.GambleCommand
import com.hakim.hakimbot.gamble.event.*
import com.hakim.hakimbot.gigabrain.GPTFacade
import com.hakim.hakimbot.gigabrain.command.AskGigaBrainCmd
import com.hakim.hakimbot.listener.CreateProfileForUserListener
import com.hakim.hakimbot.listener.TagMeListener
import com.hakim.hakimbot.network.model.UpsertProfileService
import com.hakim.hakimbot.network.model.repository.ProfileRepository
import com.hakim.hakimbot.network.service.CreateProfileForNewDiscordServerUserService
import com.hakim.hakimbot.twitter.TwitterData
import com.hakim.hakimbot.twitter.TwitterFacade
import com.hakim.hakimbot.wars.listener.CreateGeneralForNewDiscordServerUserService
import com.hakim.hakimbot.wars.model.service.TranslateModelToDomain
import com.hakim.hakimbot.wars.service.CreateGeneralFromDiscordUser
import com.hakim.hakimbot.wars.service.WarRepository
import com.hakim.hakimbot.wars.ui.command.AttackCmd
import com.hakim.hakimbot.wars.ui.command.BuyArmyUnitsCmd
import com.hakim.hakimbot.wars.ui.command.CreateGeneralForEachUser
import org.jetbrains.exposed.sql.Database
import org.kodein.di.*
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

const val LISTENER_TAG = "@listenerTag"

class Dependencies(private val args: Array<String>) {
    val dependencies = DI {
        bindSingleton { DiscordData(getArgumentValue("discordToken")) }

        bindSingleton {
            TwitterData(
                getArgumentValue("ttConsumerKey"),
                getArgumentValue("ttConsumerSecret"),
                getArgumentValue("ttAccessToken"),
                getArgumentValue("ttAccessTokenSecret"),
            )
        }

        val common = DI.Module("common") {
            bindEagerSingleton {
                Database.connect(getArgumentValue("dbUrl"), user = "root", password = "root")
            }

            bindSingleton { ExposedUtilities() }
        }

        val services = DI.Module("services") {
            bindSingleton { UpsertProfileService() }
        }

        val listeners = DI.Module("listeners") {
            bindProvider(LISTENER_TAG) { DumplingCommand() }
            bindProvider(LISTENER_TAG) { TagMeListener() }
            bindProvider(LISTENER_TAG) { CheckPPCommand() }
            bindProvider(LISTENER_TAG) { WrozbitaMaciej() }
            bindProvider(LISTENER_TAG) { TwitterCommand(instance()) }
            bindProvider(LISTENER_TAG) { CreateProfileForUserListener(instance()) }
            bindProvider(LISTENER_TAG) { GambleCommand(instance(), instance()) }
            bindProvider(LISTENER_TAG) { IsItTrue() }
            bindProvider(LISTENER_TAG) { LoveOrNot(instance()) }
            bindProvider(LISTENER_TAG) { CreateProfileForNewDiscordServerUserService() }
            bindProvider(LISTENER_TAG) { CreateProfileForEachUser() }
        }

        val gambleGame = DI.Module("gamble") {
            bindSingleton { UpsertGambleService() }
            bindProvider { Events(buildEvents()) }
        }

        val warGame = DI.Module("war") {
            bindSingleton { TranslateModelToDomain() }
            bindSingleton { CreateGeneralFromDiscordUser(instance()) }
            bindSingleton { WarRepository(instance()) }

            bindProvider(LISTENER_TAG) { AttackCmd(instance(), instance()) }
            bindProvider(LISTENER_TAG) { BuyArmyUnitsCmd(instance()) }
            bindProvider(LISTENER_TAG) { CreateGeneralForNewDiscordServerUserService(instance()) }
            bindProvider(LISTENER_TAG) { CreateGeneralForEachUser(instance()) }
        }

        val gigaBrain = DI.Module("gigabrain") {
            bindSingleton { GPTFacade(getArgumentValue("gptToken")) }
            bindSingleton(LISTENER_TAG) { AskGigaBrainCmd(instance()) }
        }

        bindSingleton { TwitterFacade(instance()) }
        bindSingleton { ProfileRepository() }

        import(gigaBrain)
        import(warGame)
        import(gambleGame)
        import(common)
        import(services)
        import(listeners)
    }

    private fun getArgumentValue(name: String): String {
        return findArgumentValue(name) ?: throw RuntimeException("$name parameter not found!")
    }

    private fun findArgumentValue(name: String): String? {
        return args.firstOrNull {
            it.startsWith("-$name=", ignoreCase = true)
        }?.substringAfter("=")
    }

    private fun buildEvents(): List<Event<*, *>> {
        return listOf<Event<*, *>>(
            Event(
                CoinsEventData(
                    "Wygra??e?? w lottto",
                    EventType.VERY_POSITIVE
                ) {
                    rewardWithRange(nextDouble(0.50, 0.80), it, nextInt(4, 10))
                },
                CoinsEventProcessor()
            ),

            Event(
                CoinsEventData(
                    "Spotka??e??/a?? jednoro??ca",
                    EventType.POSITIVE
                ) {
                    rewardWithRange(nextDouble(0.20, 0.50), it)
                },
                CoinsEventProcessor()
            ),

            Event(
                CoinsEventData(
                    "Zwrot nadp??aty",
                    EventType.NORMALLY_POSITIVE
                ) {
                    rewardWithRange(nextDouble(0.10, 0.20), it, nextInt(1, 5))
                },
                CoinsEventProcessor()
            ),

            Event(
                CoinsEventData(
                    "Kto?? Ci?? zhakowa??",
                    EventType.VERY_NEGATIVE
                ) {
                    chargeWithRange(nextDouble(0.50, 0.80), it)
                },
                CoinsEventProcessor()
            ),

            Event(
                CoinsEventData(
                    "Kto?? Ci?? obrabowa??",
                    EventType.NEGATIVE
                ) {
                    chargeWithRange(nextDouble(0.20, 0.50), it)
                },
                CoinsEventProcessor()
            ),

            Event(
                CoinsEventData(
                    "Mandacik za niedo??nie??ony komputer",
                    EventType.NEGATIVE
                ) {
                    chargeWithRange(nextDouble(0.20, 0.50), it)
                },
                CoinsEventProcessor()
            ),

            Event(
                CoinsEventData(
                    "Op??ata ZUS",
                    EventType.NORMALLY_NEGATIVE
                ) {
                    chargeWithRange(nextDouble(0.10, 0.20), it)
                },
                CoinsEventProcessor()
            )
        )
    }

    private fun rewardWithRange(range: Double, balance: Double, balanceBooster: Int? = null): Pair<Double, Double> {
        val change = range.times(balance * (balanceBooster ?: 1))

        return Pair(balance + change, change)
    }

    private fun chargeWithRange(chargePercent: Double, balance: Double): Pair<Double, Double> {
        val change = chargePercent.times(balance)

        return Pair(balance - change, change)
    }
}