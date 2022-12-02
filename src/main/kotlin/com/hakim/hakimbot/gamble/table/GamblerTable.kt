package com.hakim.hakimbot.gamble.table

import com.hakim.hakimbot.common.exposed.BaseUUIDTable
import com.hakim.hakimbot.network.model.ProfileTable

object GamblerTable : BaseUUIDTable("gamblers") {
    val profile = reference("profile", ProfileTable)
    val balance = double("balance").default(250.0)

    val lossStreak = short("loss_streak").default(0)
    val winStreak = short("win_streak").default(0)

    val lossStreakMax = short("loss_streak_max").default(0)
    val winStreakMax = short("win_streak_max").default(0)
}