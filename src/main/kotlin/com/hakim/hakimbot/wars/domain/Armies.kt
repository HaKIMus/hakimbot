package com.hakim.hakimbot.wars.domain

import com.hakim.hakimbot.wars.domain.unit.UnitType
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

    fun attack(opponentArmy: Armies): Pair<Int, Rounds> {
        val rounds = Rounds(mutableListOf())
        var opponentArmyHealth = opponentArmy.totalArmyHealth
        var armyHealth = totalArmyHealth

        while (opponentArmyHealth > 0 && armyHealth > 0) {
            val armyDamageChunks = randomTotalArmyDamage(army) / 2
            val opponentArmyDamageChunks = randomTotalArmyDamage(opponentArmy.army) / 2

            opponentArmyHealth -= armyDamageChunks.roundToInt()
            armyHealth -= opponentArmyDamageChunks.roundToInt()

            val round = Round(
                armyDamageChunks,
                opponentArmyDamageChunks,
                opponentArmyHealth,
                armyHealth
            )

            rounds.appendRound(round)
        }

        return Pair(armyHealth compareTo opponentArmyHealth, rounds)
    }

    private fun randomTotalArmyDamage(army: List<Army>): Double {
        val meleeDmg = army.filter { it.unit.type == UnitType.MELEE }.sumOf {
            it.unit.damage.randomDamageBetweenRange() * it.amount
        }

        val rangeDmg = army.filter { it.unit.type == UnitType.RANGE }.sumOf {
            it.unit.damage.randomDamageBetweenRange() * it.amount
        }

        return meleeDmg + rangeDmg
    }
}
