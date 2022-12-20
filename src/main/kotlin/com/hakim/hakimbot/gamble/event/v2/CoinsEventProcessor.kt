package com.hakim.hakimbot.gamble.event.v2

import com.hakim.hakimbot.formatDouble
import com.hakim.hakimbot.gamble.Gambler
import org.jetbrains.exposed.sql.transactions.transaction

class CoinsEventProcessor : EventProcessor<CoinsEventData, Double> {
    override fun process(data: CoinsEventData, gambler: Gambler): Double {
        return transaction {
            val result = data.coinsTransform(gambler.balance)
            gambler.balance = result.first

            if (data.eventType.value > 0) {
                data.message = "Zwrócono Ci **${formatDouble(result.second)}** żetonów!"
            } else {
                data.message = "Skradziono Ci **${formatDouble(result.second)}** żetonów!"
            }

            return@transaction gambler.balance
        }
    }
}