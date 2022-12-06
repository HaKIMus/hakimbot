package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.properties.Delegates
import kotlin.random.Random

class SomeoneRobbedYou : Event {
    override val name: String = "Ktoś Cię obrabował!"
    override val description: String = "Almost had a thousand dollars 'til someone broke in and stole it and I know it hurt so bad, it broke your momma's heart"
    override val type: EventType = EventType.NEGATIVE
    private val chargePercent = Random.nextDouble(0.20, 0.50)
    private var result by Delegates.notNull<Double>()

    override fun onApply(gambler: Gambler) {
        transaction {
            result = chargePercent.times(gambler.balance)
            gambler.balance = (gambler.balance - result)
        }
    }

    override fun message(gambler: Gambler): String {
        return "Skradziono Ci **${formatDouble(result)} żetonów**. Almost had a thousand dollars 'til someone broke in and stole it and it broke your momma's heart."
    }
}