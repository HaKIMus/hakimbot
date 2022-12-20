package wars.domain

import com.hakim.hakimbot.network.model.Profile
import com.hakim.hakimbot.network.model.ProfileTable
import com.hakim.hakimbot.wars.domain.Armies
import com.hakim.hakimbot.wars.domain.Army
import com.hakim.hakimbot.wars.domain.General
import com.hakim.hakimbot.wars.domain.unit.Unit
import com.hakim.hakimbot.wars.domain.unit.UnitAttackProtection
import com.hakim.hakimbot.wars.domain.unit.UnitDamageRange
import com.hakim.hakimbot.wars.domain.unit.UnitType
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*
import kotlin.test.Test


class WarTest {
    @Test
    fun testWar() {
        val armies = Armies(
            listOf(
                Army(
                    Unit(
                        UnitType.MELEE,
                        "Solider",
                        21,
                        UnitAttackProtection(6),
                        UnitAttackProtection(5),
                        UnitDamageRange(2.0, 10.0),
                        10.0,
                    ),
                    200
                )
            )
        )

        val attacker = General(
            UUID.randomUUID(),
            Profile(EntityID(UUID.randomUUID(), ProfileTable)),
            armies,
            100,
        )

        val defender = General(
            UUID.randomUUID(),
            Profile(EntityID(UUID.randomUUID(), ProfileTable)),
            armies,
            100
        )

        val warResult = attacker.attack(defender)
        println(warResult.result)
    }
}