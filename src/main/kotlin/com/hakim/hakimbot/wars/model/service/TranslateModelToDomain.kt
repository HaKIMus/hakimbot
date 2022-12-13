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
import com.hakim.hakimbot.wars.model.UnitModel
import com.hakim.hakimbot.wars.model.table.ArmyTable
import org.jetbrains.exposed.sql.SizedIterable

class TranslateModelToDomain {
    fun translateGeneralModelToDomain(model: GeneralModel): General {
        return General(
            model.uuid,
            translateArmyModelToArmiesDomain(ArmyModel.find { ArmyTable.general eq model.uuid }),
            model.honorPoints
        )
    }

    private fun translateArmyModelToArmiesDomain(armyModels: SizedIterable<ArmyModel>): Armies {
        return Armies(
            armyModels.toList()
                .map {
                    Army(
                        translateUnitModelToUnitDomain(it.unit),
                        it.amount
                    )
                }
        )
    }

    private fun translateUnitModelToUnitDomain(unitModel: UnitModel): Unit {
        return Unit(
            UnitType.valueOf(unitModel.type),
            unitModel.name,
            unitModel.healthPoints,
            UnitAttackProtection(unitModel.meleeProtection),
            UnitAttackProtection(unitModel.rangeProtection),
            UnitDamageRange(
                unitModel.damageMin,
                unitModel.damageMax,
            )
        )
    }
}