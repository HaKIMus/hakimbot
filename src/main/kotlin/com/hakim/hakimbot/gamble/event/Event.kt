package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.gamble.Gambler

interface Event {
    val name: String

    val description: String

    val type: EventType

    fun onApply(gambler: Gambler)

    fun message(gambler: Gambler): String
}