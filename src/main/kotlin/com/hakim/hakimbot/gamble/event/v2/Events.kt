package com.hakim.hakimbot.gamble.event.v2

import com.hakim.hakimbot.gamble.event.EventType
import kotlin.math.abs
import kotlin.random.Random

class Events(private val events: List<Event<*, *>>) {
    fun randomEvent(): Event<*, *>? {
        val randomizedEvents = events.associateBy {
            randomizeByType(it)
        }

        return randomizedEvents
            .filterKeys { it }
            .values
            .maxByOrNull { abs(it.data.eventType.value) }
    }

    private fun randomizeByType(event: Event<*, *>): Boolean {
        return when (event.data.eventType) {
            EventType.VERY_POSITIVE -> randomize(3.00)
            EventType.POSITIVE -> randomize(5.00)
            EventType.NORMALLY_POSITIVE -> randomize(10.00)
            EventType.NORMALLY_NEGATIVE -> randomize(12.0)
            EventType.NEGATIVE -> randomize(6.00)
            EventType.VERY_NEGATIVE -> randomize(4.00)
        }
    }

    private fun randomize(chance: Double): Boolean {
        return Random.nextDouble(0.01, 100.0) < chance
    }
}