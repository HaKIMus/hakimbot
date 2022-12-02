package com.hakim.hakimbot.gamble.table

import com.hakim.hakimbot.common.exposed.BaseUUIDTable
import com.hakim.hakimbot.network.model.ProfileTable

object GamblerTable : BaseUUIDTable("gamblers") {
    val profile = reference("profile", ProfileTable)
    val balance = double("balance").default(250.0)
}