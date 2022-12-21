package com.hakim.hakimbot.gamble.event

import com.hakim.hakimbot.gamble.Gambler

class Event<T : EventData, P : EventProcessor<T>> (
    val data: T,
    val processor: P,
){
    fun process(gambler: Gambler) { processor.process(data, gambler) }
}