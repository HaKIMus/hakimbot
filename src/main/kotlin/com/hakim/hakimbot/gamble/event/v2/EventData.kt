package com.hakim.hakimbot.gamble.event.v2

import com.hakim.hakimbot.gamble.event.EventType

interface EventData {
    val eventName: String
    val eventType: EventType
    var message: String?
}