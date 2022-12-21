package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.gamble.Gambler

interface EventProcessor<T : EventData> {
    fun process(data: T, gambler: Gambler)
}