package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

class SomeoneRobbedYou : Event {
    override val name: String = "Ktoś Cię obrabował!"
    override val description: String = "Almost had a thousand dollars 'til someone broke in and stole it and I know it hurt so bad, it broke your momma's heart"
    override val type: EventType = EventType.NEGATIVE

    private val chargePercent = Random.nextDouble(0.20, 0.50)

    override fun onApply(gambler: Gambler) {
        transaction {
            gambler.balance = (gambler.balance - (chargePercent.times(gambler.balance)))
        }
    }

    override fun message(gambler: Gambler): String {
        return "Skradziono Ci **${(chargePercent.times(gambler.balance))} żetonów**. Almost had a thousand dollars 'til someone broke in and stole it and it broke your momma's heart."
    }
}