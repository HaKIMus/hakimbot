package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.properties.Delegates
import kotlin.random.Random

class SomeoneHackedYou : Event {
    override val name: String = "Ktoś Cię zhakował!"
    override val description: String = "Ktoś Cię zhakował!"
    override val type: EventType = EventType.VERY_NEGATIVE
    private val chargePercent = Random.nextDouble(0.50, 0.80)
    private var result by Delegates.notNull<Double>()

    override fun onApply(gambler: Gambler) {
        transaction {
            result = chargePercent.times(gambler.balance)
            gambler.balance = (gambler.balance - result)
        }
    }

    override fun message(gambler: Gambler): String {
        return "Skradziono Ci **${formatDouble(result)} żetonów**."
    }
}