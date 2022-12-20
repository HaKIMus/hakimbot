package com.hakim.hakimbot.wars.domain

data class Round(
    val attackerDamage: Double,
    val opponentDamage: Double,
    val attackerHealth: Int,
    val opponentHealth: Int,
)