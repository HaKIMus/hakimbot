package com.hakim.hakimbot.wars.domain

import java.util.UUID

class General(
    val id: UUID,
    val army: Armies,
    private var _honorPoints: Int,
) {
    val honorPoints = _honorPoints

    fun attack(opponent: General): WarResult {
        val war = War.between(this, opponent)

        return war.start()
    }
}