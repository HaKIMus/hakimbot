package com.hakim.hakimbot.wars.domain.unit

@JvmInline
value class UnitAttackProtection(val protectionPercentage: Int) {
    init {
        require(protectionPercentage in 0..100)
    }
}