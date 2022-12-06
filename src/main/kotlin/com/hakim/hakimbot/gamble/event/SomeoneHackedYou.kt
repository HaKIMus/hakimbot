package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

class SomeoneHackedYou : Event {
    override val name: String = "Ktoś Cię zhakował!"
    override val description: String = "Ktoś Cię zhakował!"
    override val type: EventType = EventType.VERY_NEGATIVE
    private val chargePercent = Random.nextDouble(0.50, 0.80)

    override fun onApply(gambler: Gambler) {
        transaction {
            gambler.balance = (gambler.balance - (chargePercent.times(gambler.balance)))
        }
    }

    override fun message(gambler: Gambler): String {
        return "Skradziono Ci **${formatDouble(chargePercent.times(gambler.balance))} żetonów**."
    }
}