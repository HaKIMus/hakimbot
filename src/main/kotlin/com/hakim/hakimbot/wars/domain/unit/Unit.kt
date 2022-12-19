package com.hakim.hakimbot.wars.domain.unit

class Unit(
    val type: UnitType,
    val name: String,
    val healthPoints: Int,
    val meleeProtection: UnitAttackProtection,
    val rangeProtection: UnitAttackProtection,
    val damage: UnitDamageRange,
    val price: Double,
)