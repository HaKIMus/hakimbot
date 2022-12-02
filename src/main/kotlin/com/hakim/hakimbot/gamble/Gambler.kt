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
import java.util.*
import kotlin.random.Random

class Gambler(id: EntityID<UUID>) : BaseUuidEntity(id, GamblerTable){
    companion object: BaseUuidEntityClass<Gambler>(GamblerTable)

    val uuid = id.value
    var profile by Profile referencedOn GamblerTable.profile
    var balance by GamblerTable.balance

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
                balance = (balance + (amount * 2.0))
                return@transaction GambleResult(true, amount, amount * 2.0)
            } else {
                balance = (balance - amount)
                return@transaction GambleResult(false, amount, amount)
            }
        }
    }
}