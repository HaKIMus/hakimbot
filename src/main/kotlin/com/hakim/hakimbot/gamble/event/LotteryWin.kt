package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.properties.Delegates
import kotlin.random.Random

class LotteryWin : Event {
    override val name: String = "Wygrałeś w lotto!"
    override val description: String = "Wygrałeś w lotto!"
    override val type: EventType = EventType.VERY_POSITIVE
    private val chargePercent = Random.nextDouble(0.50, 0.80)
    private var result by Delegates.notNull<Double>()

    override fun onApply(gambler: Gambler) {
        transaction {
            result = chargePercent.times(gambler.balance)
            gambler.balance = (gambler.balance + result)
        }
    }

    override fun message(gambler: Gambler): String {
        return "Wygrałeś **${formatDouble(result)} żetonów**."
    }
}