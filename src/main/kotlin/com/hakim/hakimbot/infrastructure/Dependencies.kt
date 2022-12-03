package com.hakim.hakimbot.infrastructure

import com.hakim.hakimbot.DiscordData
import com.hakim.hakimbot.command.CheckPPCommand
import com.hakim.hakimbot.command.DumplingCommand
import com.hakim.hakimbot.command.TwitterCommand
import com.hakim.hakimbot.command.WrozbitaMaciej
import com.hakim.hakimbot.gamble.RandomEvents
import com.hakim.hakimbot.gamble.UpsertGambleService
import com.hakim.hakimbot.gamble.command.GambleCommand
import com.hakim.hakimbot.gamble.event.SomeoneRobbedYou
import com.hakim.hakimbot.gamble.event.TaxReturn
import com.hakim.hakimbot.gamble.event.ZusCharge
import com.hakim.hakimbot.listener.CreateProfileForUserListener
import com.hakim.hakimbot.listener.TagMeListener
import com.hakim.hakimbot.network.model.UpsertProfileService
import com.hakim.hakimbot.twitter.TwitterData
import com.hakim.hakimbot.twitter.TwitterFacade
import org.jetbrains.exposed.sql.Database
import org.kodein.di.*

const val LISTENER_TAG = "@listenerTag"
const val GAMBLE_RANDOM_EVENT_TAG = "@gambleRandomEvent"

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
        }

        val gambleGame = DI.Module("gamble") {
            bindSingleton { UpsertGambleService() }

            bindProvider(GAMBLE_RANDOM_EVENT_TAG) { ZusCharge() }
            bindProvider(GAMBLE_RANDOM_EVENT_TAG) { TaxReturn() }
            bindProvider(GAMBLE_RANDOM_EVENT_TAG) { SomeoneRobbedYou() }

            bindProvider { RandomEvents(allInstances(GAMBLE_RANDOM_EVENT_TAG)) }
        }

        bindSingleton { TwitterFacade(instance()) }

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
}