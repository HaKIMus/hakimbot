package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.properties.Delegates
import kotlin.random.Random

class ZusCharge : Event {
    override val name: String = "Opłata ZUS"
    override val description: String = "Hej, jak będziesz stary to dostaniesz coś z tego i może starczy na ogrzewanie!"
    override val type: EventType = EventType.NEGATIVE
    private val chargePercent = Random.nextDouble(0.10, 0.20)
    private var result by Delegates.notNull<Double>()

    override fun onApply(gambler: Gambler) {
        transaction {
            result = chargePercent.times(gambler.balance)
            gambler.balance = (gambler.balance - result)
        }
    }

    override fun message(gambler: Gambler): String {
        return "Opłata ZUS wyniosła Cię **${formatDouble(result)} żetonów**!"
    }
}