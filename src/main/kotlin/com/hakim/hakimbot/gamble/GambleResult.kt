package com.hakim.hakimbot.gamble

data class GambleResult(
    val hasWon: Boolean,
    val investment: Double,
    val outcome: Double,
)