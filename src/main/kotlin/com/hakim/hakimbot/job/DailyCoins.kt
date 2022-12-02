package com.hakim.hakimbot.job

import com.hakim.hakimbot.gamble.Gambler
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.hours

class DailyCoins {
    suspend fun execute() {
        while (true) {
            transaction {
                Gambler.all().forEach {
                    it.balance = it.balance + 1000
                }
            }

            delay(24.hours)
        }
    }
}