package com.hakim.hakimbot.common.exposed

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun currentUtc(): LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))

abstract class BaseUUIDTable(name: String) : IdTable<UUID>(name) {
    override val id: Column<EntityID<UUID>> = uuid("id").entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }

    val createdAt = datetime("createdAt").clientDefault { currentUtc() }
    val updatedAt = datetime("updatedAt").nullable()
}