package com.hakim.hakimbot.wars.model.table

import com.hakim.hakimbot.common.exposed.BaseUUIDTable
import com.hakim.hakimbot.network.model.Profile.Companion.referrersOn
import com.hakim.hakimbot.network.model.ProfileTable
import com.hakim.hakimbot.wars.model.ArmyModel

object GeneralTable : BaseUUIDTable("wars_generals") {
    val profile = reference("profile", ProfileTable)
    val honorPoints = integer("honor_points")
}