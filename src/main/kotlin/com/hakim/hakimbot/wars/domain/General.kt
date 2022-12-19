package com.hakim.hakimbot.wars.domain

import com.hakim.hakimbot.network.model.Profile
import java.util.*

class General(
    val id: UUID,
    val profile: Profile,
    val army: Armies,
    private var _honorPoints: Int,
) {
    val honorPoints = _honorPoints

    fun attack(opponent: General): WarResult {
        val war = War.between(this, opponent)

        return war.start()
    }
}