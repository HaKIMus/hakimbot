package com.hakim.hakimbot.gamble

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
import com.hakim.hakimbot.gamble.event.Events
import com.hakim.hakimbot.gamble.exception.*
import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.Profile
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Double.max
import java.util.*
import kotlin.random.Random

class Gambler(id: EntityID<UUID>) : BaseUuidEntity(id, GamblerTable) {
    companion object : BaseUuidEntityClass<Gambler>(GamblerTable)

    val uuid = id.value
    var profile by Profile referencedOn GamblerTable.profile
    var balance by GamblerTable.balance

    var lossStreak by GamblerTable.lossStreak
    var winStreak by GamblerTable.winStreak

    var lossStreakMax by GamblerTable.lossStreakMax
    var winStreakMax by GamblerTable.winStreakMax

    fun gamble(balancePercentage: Int, events: Events): GambleResult {
        if (balancePercentage !in 10..100) {
            throw InvalidPercentageRangeException(null)
        }

        if (0 >= balance) {
            throw NoBalanceException(null)
        }

        val p = balancePercentage / 100.0
        return gamble(p.times(balance), events, true)
    }

    fun gamble(amount: Double, events: Events, wasValidated: Boolean = false): GambleResult {
        if (!wasValidated) {
            if (amount < 0.10.times(balance)) {
                throw Minimum10PercentOfBalance(null)
            }
        }

        if (0 >= amount) {
            throw NoAmountException(null)
        }

        if (amount > balance) {
            throw NotSufficientBalanceException(null)
        }

        return transaction {
            val hasWon = Random.nextBoolean()

            val event = events.randomEvent()
            event?.process(this@Gambler)

            if (hasWon) {
                resetLossStreak()
                winStreak = (winStreak + 1).toShort()
                val winStreakBonus = ((winStreak * 10.0) / 100.0).times(amount)
                balance = (balance + ((amount * 2.0) + winStreakBonus))
                return@transaction GambleResult(true, amount, ((amount * 2.0) + winStreakBonus), winStreak, event)
            } else {
                resetWinStreak()
                lossStreak = (lossStreak + 1).toShort()
                val lossStreakPenalty = ((lossStreak * 10.0) / 100.0).times(amount)
                val lossStreakCopy =
                    lossStreak // resetLossStreak resets the loss streaks so when it's passed to GameResult it's shown as 0 instead of the current loss streak
                balance = max((balance - (amount + lossStreakPenalty)), 0.0)

                if (balance == 0.0) {
                    resetLossStreak()
                }

                return@transaction GambleResult(
                    false,
                    amount,
                    max(amount + lossStreakPenalty, 0.0),
                    lossStreakCopy,
                    event
                )
            }
        }
    }

    fun borrowTo(gambler: Gambler, amount: Double) {
        transaction {
            require(amount <= balance) {
                "Nie posiadasz wystarczającej ilości gotówki!"
            }

            balance = (balance - amount)
            gambler.balance = gambler.balance + amount
        }
    }

    fun buy(product: Product) {
        transaction {
            require(balance >= product.price) {
                "Nie posiadasz wystarczającej ilości gotówki!"
            }

            balance = (balance - product.price)
        }
    }

    private fun resetWinStreak() {
        if (winStreak > winStreakMax) {
            winStreakMax = winStreak
        }

        winStreak = 0
    }

    private fun resetLossStreak() {
        if (lossStreak > lossStreakMax) {
            lossStreakMax = lossStreak
        }

        lossStreak = 0
    }
}