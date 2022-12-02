package com.hakim.hakimbot.gamble

import com.hakim.hakimbot.common.exposed.BaseUuidEntity
import com.hakim.hakimbot.common.exposed.BaseUuidEntityClass
import com.hakim.hakimbot.gamble.exception.InvalidPercentageRangeException
import com.hakim.hakimbot.gamble.exception.NoAmountException
import com.hakim.hakimbot.gamble.exception.NoBalanceException
import com.hakim.hakimbot.gamble.exception.NotSufficientBalanceException
import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.Profile
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Double.max
import java.util.*
import kotlin.random.Random

class Gambler(id: EntityID<UUID>) : BaseUuidEntity(id, GamblerTable){
    companion object: BaseUuidEntityClass<Gambler>(GamblerTable)

    val uuid = id.value
    var profile by Profile referencedOn GamblerTable.profile
    var balance by GamblerTable.balance

    var lossStreak by GamblerTable.lossStreak
    var winStreak by GamblerTable.winStreak

    var lossStreakMax by GamblerTable.lossStreakMax
    var winStreakMax by GamblerTable.winStreakMax

    fun gamble(balancePercentage: Int): GambleResult {
        if (balancePercentage !in 1..100) {
            throw InvalidPercentageRangeException(null)
        }

        if (0 >= balance) {
            throw NoBalanceException(null)
        }

        val p = balancePercentage / 100.0
        return gamble(p.times(balance))
    }

    /**
     * 2:1 win/loss ratio
     */
    fun gamble(amount: Double): GambleResult {
        if (0 >= amount) {
            throw NoAmountException(null)
        }

        if (amount > balance) {
            throw NotSufficientBalanceException(null)
        }

        return transaction {
            val hasWon = Random.nextBoolean()

            if (hasWon) {
                resetLossStreak()
                winStreak = (winStreak + 1).toShort()
                balance = (balance + ((amount * 2.0) * winStreak))
                return@transaction GambleResult(true, amount, (amount * 2.0) * winStreak, winStreak)
            } else {
                resetWinStreak()
                lossStreak = (lossStreak + 1).toShort()
                val lossStreakCopy = lossStreak // resetLossStreak resets the loss streaks so when it's passed to GameResult it's shown as 0 instead of the current loss streak
                balance = max((balance - (amount * lossStreak)), 0.0)

                if (balance == 0.0) {
                    resetLossStreak()
                }

                return@transaction GambleResult(false, amount, max((amount * lossStreak), 0.0), lossStreakCopy)
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