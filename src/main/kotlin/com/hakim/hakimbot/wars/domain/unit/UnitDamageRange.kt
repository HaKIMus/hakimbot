package com.hakim.hakimbot.wars.domain.unit

import kotlin.random.Random

data class UnitDamageRange(val minDamage: Double, val maxDamage: Double) {
    init {
        require(minDamage >= 0)
        require(maxDamage >= minDamage)
    }

    fun randomDamageBetweenRange(): Double = Random.nextDouble(minDamage, maxDamage)
}