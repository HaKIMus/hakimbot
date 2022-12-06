package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.properties.Delegates
import kotlin.random.Random

class TaxReturn : Event {
    override val name: String = "Zwrot nadpłaty!"
    override val description: String = "Pan z kotem Cię szuka!"
    override val type: EventType = EventType.POSITIVE
    private val chargePercent = Random.nextDouble(0.10, 0.20)
    private var result by Delegates.notNull<Double>()

    override fun onApply(gambler: Gambler) {
        transaction {
            result = chargePercent.times(gambler.balance)
            gambler.balance = (gambler.balance + result)
        }
    }

    override fun message(gambler: Gambler): String {
        return "Server zwrócił Ci **${formatDouble(result)} żetonów** nadpłaty!"
    }
}