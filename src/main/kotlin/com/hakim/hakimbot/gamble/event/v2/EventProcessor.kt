package com.hakim.hakimbot.gamble.event.v2

import com.hakim.hakimbot.gamble.Gambler

interface EventProcessor<T : EventData, R> {
    fun process(data: T, gambler: Gambler): R
}