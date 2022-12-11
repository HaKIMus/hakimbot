package com.hakim.hakimbot.wars.model.service

import com.hakim.hakimbot.wars.domain.Armies
import com.hakim.hakimbot.wars.domain.Army
import com.hakim.hakimbot.wars.domain.General
import com.hakim.hakimbot.wars.domain.unit.Unit
import com.hakim.hakimbot.wars.domain.unit.UnitAttackProtection
import com.hakim.hakimbot.wars.domain.unit.UnitDamageRange
import com.hakim.hakimbot.wars.domain.unit.UnitType
import com.hakim.hakimbot.wars.model.ArmyModel
import com.hakim.hakimbot.wars.model.GeneralModel
import com.hakim.hakimbot.wars.model.table.ArmyTable
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TranslateModelToDomain {
    fun translateGeneralModelToDomain(model: GeneralModel): General {
        return General(
            model.uuid,
            xdfsdfgsd(ArmyModel.find { ArmyTable.general eq model.uuid }),
            model.honorPoints
        )
    }

    private fun xdfsdfgsd(x: SizedIterable<ArmyModel>): Armies {
        return Armies(
            x.toList()
                .map {
                    Army(
                        Unit(
                            UnitType.valueOf(it.unit.type),
                            it.unit.name,
                            it.unit.healthPoints,
                            UnitAttackProtection(it.unit.meleeProtection),
                            UnitAttackProtection(it.unit.rangeProtection),
                            UnitDamageRange(
                                it.unit.damageMin,
                                it.unit.damageMax,
                            )
                        ),
                        it.amount
                    )
                }
        )
    }
}