package com.hakim.hakimbot.gamble

import com.hakim.hakimbot.gamble.event.v2.Event

data class GambleResult(
    val hasWon: Boolean,
    val investment: Double,
    val outcome: Double,
    val streak: Short,
    val event: Event<*, *>?
)