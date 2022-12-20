package com.hakim.hakimbot.gamble.event.v2

import com.hakim.hakimbot.gamble.event.EventType

class CoinsEventData(
    override val eventName: String,
    override val eventType: EventType,
    override var message: String? = null,
    val coinsTransform: (Double) -> Pair<Double, Double>, // FIRST: NEW BALANCE, SECOND: CHANGE
) : EventData