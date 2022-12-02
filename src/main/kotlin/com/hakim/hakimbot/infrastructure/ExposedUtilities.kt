package com.hakim.hakimbot.infrastructure

import com.hakim.hakimbot.gamble.table.GamblerTable
import com.hakim.hakimbot.network.model.ProfileTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedUtilities {
    fun createMissingTablesAndColumns() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                ProfileTable,
                GamblerTable,
            )
        }
    }
}