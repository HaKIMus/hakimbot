package com.hakim.hakimbot.wars.domain

import com.hakim.hakimbot.randomize
import com.hakim.hakimbot.wars.domain.unit.UnitType
import kotlin.math.max
import kotlin.math.roundToInt

class Armies(private val army: List<Army>) : Comparable<Armies> {
    private var meleeHealth = army.filter { it.unit.type == UnitType.MELEE }.sumOf {
        it.unit.healthPoints * it.amount
    }

    private var rangeHealth = army.filter { it.unit.type == UnitType.RANGE }.sumOf {
        it.unit.healthPoints * it.amount
    }

    private var meleeDamage = army.filter { it.unit.type == UnitType.MELEE }.sumOf {
        it.unit.damage.randomDamageBetweenRange() * it.amount
    }

    private val rangeDamage = army.filter { it.unit.type == UnitType.RANGE }.sumOf {
        it.unit.damage.randomDamageBetweenRange() * it.amount
    }

    private var totalArmyHealth = meleeHealth + rangeHealth
    private var totalArmyDamage = meleeDamage + rangeDamage

    override fun compareTo(other: Armies): Int {
        val x = totalArmyHealth compareTo other.totalArmyHealth
        val y = totalArmyDamage compareTo other.totalArmyDamage

        return x + y
    }

    /**
     * @TODO Refactor
     */
    fun attack(opponentArmy: Armies): Pair<Int, Rounds> {
        val rounds = Rounds(mutableListOf())
        var opponentArmyHealth = opponentArmy.totalArmyHealth
        var armyHealth = totalArmyHealth

        while (opponentArmyHealth > 0 && armyHealth > 0) {
            val armyDamageChunks = randomTotalArmyDamage(army, true) / 2
            var opponentArmyDamageChunks = randomTotalArmyDamage(opponentArmy.army, false) / 2

            opponentArmyHealth -= armyDamageChunks.roundToInt()
            if (opponentArmyHealth > 0) {
                armyHealth -= opponentArmyDamageChunks.roundToInt()
            } else {
                opponentArmyDamageChunks = 0.0
            }

            val round = Round(
                armyDamageChunks,
                opponentArmyDamageChunks,
                max(armyHealth, 0),
                max(opponentArmyHealth, 0),
            )

            rounds.appendRound(round)
        }

        return Pair(max(armyHealth, 0) compareTo max(opponentArmyHealth, 0), rounds)
    }

    private fun randomTotalArmyDamage(army: List<Army>, isAttackerArmy: Boolean): Double {
        var meleeDmg = army.filter { it.unit.type == UnitType.MELEE }.sumOf {
            it.unit.damage.randomDamageBetweenRange() * it.amount
        }

        var rangeDmg = army.filter { it.unit.type == UnitType.RANGE }.sumOf {
            it.unit.damage.randomDamageBetweenRange() * it.amount
        }

        if (randomize(10.0)) {
            meleeDmg += 0.50 * meleeDmg
            rangeDmg += 0.50 * rangeDmg
        }

        if (!isAttackerArmy) {
            meleeDmg = applyDefenderDamageBuff(meleeDmg)
            rangeDmg = applyDefenderDamageBuff(rangeDmg)
        }

        return meleeDmg + rangeDmg
    }

    private fun applyDefenderDamageBuff(rawDamage: Double): Double {
        val buff = 0.10 * rawDamage

        return rawDamage + buff
    }
}
