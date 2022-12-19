package com.hakim.hakimbot.wars.domain

import java.time.LocalDateTime

class War private constructor(
    val attacker: General,
    val defender: General,
    val startedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun start(): WarResult {
        val attackResult = attacker.army.attack(defender.army)
        var warResultType = WarResultType.DRAW

        if (attackResult.first > 0) {
            warResultType = WarResultType.ATTACKER_WON
        } else if (attackResult.first < 0) {
            warResultType = WarResultType.DEFENDER_WON
        }

        return WarResult(
            attacker,
            defender,
            warResultType,
            attackResult.second,
        )
    }

    companion object {
        fun between(general: General, opponent: General): War {
            return War(general, opponent)
        }
    }
}