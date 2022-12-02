package com.hakim.hakimbot.common.exposed.fixtures

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AbstractSoftFixture : Fixture {
    fun <T : Table> isSkipRequired(table: T): Boolean {
        return transaction {
            if (table.selectAll().count() >= 1) {
                return@transaction true
            }

            return@transaction false
        }
    }
}