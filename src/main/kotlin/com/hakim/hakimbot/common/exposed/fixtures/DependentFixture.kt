package com.hakim.hakimbot.common.exposed.fixtures

interface DependentFixture : Fixture {
    fun dependingOn(): Int
}