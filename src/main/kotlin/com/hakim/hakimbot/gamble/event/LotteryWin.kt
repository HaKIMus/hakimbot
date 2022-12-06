package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

class LotteryWin : Event {
    override val name: String = "Wygrałeś w lotto!"
    override val description: String = "Wygrałeś w lotto!"
    override val type: EventType = EventType.VERY_POSITIVE
    private val chargePercent = Random.nextDouble(0.50, 0.80)

    override fun onApply(gambler: Gambler) {
        transaction {
            gambler.balance = (gambler.balance + (chargePercent.times(gambler.balance)))
        }
    }

    override fun message(gambler: Gambler): String {
        return "Wygrałeś **${formatDouble(chargePercent.times(gambler.balance))} żetonów**."
    }
}