package com.hakim.hakimbot.wars.domain

import java.util.SortedSet

data class Rounds(private val _rounds: MutableList<Round>) {
    val rounds: List<Round> = _rounds

    fun appendRound(round: Round) {
        _rounds.add(round)
    }
}