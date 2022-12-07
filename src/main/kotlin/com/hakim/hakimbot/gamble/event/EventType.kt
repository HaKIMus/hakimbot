package com.hakim.hakimbot.gamble.event

enum class EventType(val value: Int) {
    VERY_POSITIVE(3),
    POSITIVE(2),
    NORMALLY_POSITIVE(1),
    NORMALLY_NEGATIVE(-1),
    NEGATIVE(-2),
    VERY_NEGATIVE(-3)
}