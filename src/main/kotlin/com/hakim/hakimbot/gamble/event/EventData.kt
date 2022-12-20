package com.hakim.hakimbot.gamble.event

interface EventData {
    val eventName: String
    val eventType: EventType
    var message: String?
}