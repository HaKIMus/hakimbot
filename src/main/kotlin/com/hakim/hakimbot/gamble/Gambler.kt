package com.hakim.hakimbot.gamble

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
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

    fun gamble(balancePercentage: Int, randomEvents: RandomEvents): GambleResult {
        if (balancePercentage !in 10..100) {
            throw InvalidPercentageRangeException(null)
        }

        if (0 >= balance) {
            throw NoBalanceException(null)
        }

        val p = balancePercentage / 100.0
        return gamble(p.times(balance), randomEvents, true)
    }

    fun gamble(amount: Double, randomEvents: RandomEvents, wasValidated: Boolean = false): GambleResult {
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

            val event = randomEvents.randomEvent()
            event?.onApply(this@Gambler)

            if (hasWon) {
                resetLossStreak()
                winStreak = (winStreak + 1).toShort()
                val winStreakBonus = (winStreak / 100.0).times(amount)
                balance = (balance + ((amount * 2.0) + winStreakBonus))
                return@transaction GambleResult(true, amount, (amount * 2.0) * winStreak, winStreak, event)
            } else {
                resetWinStreak()
                lossStreak = (lossStreak + 1).toShort()
                val lossStreakPenalty = (lossStreak / 100.0).times(amount)
                val lossStreakCopy =
                    lossStreak // resetLossStreak resets the loss streaks so when it's passed to GameResult it's shown as 0 instead of the current loss streak
                balance = max((balance - (amount + lossStreakPenalty)), 0.0)

                if (balance == 0.0) {
                    resetLossStreak()
                }

                return@transaction GambleResult(
                    false,
                    amount,
                    max((amount * max(lossStreak / 2.0, 1.0)), 0.0),
                    lossStreakCopy,
                    event
                )
            }
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