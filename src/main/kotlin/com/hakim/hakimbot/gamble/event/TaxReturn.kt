package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

class TaxReturn : Event {
    override val name: String = "Zwrot nadpłaty!"
    override val description: String = "Pan z kotem Cię szuka!"
    override val type: EventType = EventType.POSITIVE
    private val chargePercent = Random.nextDouble(0.10, 0.20)

    override fun onApply(gambler: Gambler) {
        transaction {
            gambler.balance = (gambler.balance + (chargePercent.times(gambler.balance)))
        }
    }

    override fun message(gambler: Gambler): String {
        return "Server zwrócił Ci **${(chargePercent.times(gambler.balance))} żetonów** nadpłaty!"
    }
}