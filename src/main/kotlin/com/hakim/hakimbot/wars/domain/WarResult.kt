package com.hakim.hakimbot.wars.domain

class WarResult(
    val attacker: General,
    val defender: General,
    val result: WarResultType,
    val rounds: Rounds,
)

enum class WarResultType {
    ATTACKER_WON,
    DEFENDER_WON,
    DRAW
}