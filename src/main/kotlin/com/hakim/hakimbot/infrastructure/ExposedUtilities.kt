package com.hakim.hakimbot.infrastructure

import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.ProfileTable
import com.hakim.hakimbot.wars.domain.War
import com.hakim.hakimbot.wars.model.table.ArmyTable
import com.hakim.hakimbot.wars.model.table.GeneralTable
import com.hakim.hakimbot.wars.model.table.UnitTable
import com.hakim.hakimbot.wars.model.table.WarTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedUtilities {
    fun createMissingTablesAndColumns() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                ProfileTable,
                GamblerTable,

                GeneralTable,
                WarTable,
                UnitTable,
                ArmyTable,
            )
        }
    }
}