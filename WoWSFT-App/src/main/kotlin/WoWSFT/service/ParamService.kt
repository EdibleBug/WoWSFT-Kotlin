package WoWSFT.service

import WoWSFT.model.Constant.*
import WoWSFT.model.custom.CustomSkill
import WoWSFT.model.gameparams.CommonModifier
import WoWSFT.model.gameparams.CommonModifierShip
import WoWSFT.model.gameparams.flag.Flag
import WoWSFT.model.gameparams.ship.Ship
import WoWSFT.model.gameparams.ship.component.airdefense.AAJoint
import WoWSFT.model.gameparams.ship.component.airdefense.Aura
import WoWSFT.model.gameparams.ship.component.planes.Plane
import WoWSFT.model.gameparams.ship.component.torpedo.Launcher
import WoWSFT.utils.CommonUtils.getBonus
import WoWSFT.utils.CommonUtils.getDecimalRounded
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.*

@Service
class ParamService(
    @Qualifier(TYPE_FLAG) private val flagsLHM: LinkedHashMap<String, Flag>,
    @Qualifier(CUSTOM_SKIlL_GROUP) private val skillGroup: HashMap<String, List<List<CustomSkill>>>
)
{
    companion object {
        private val mapper = ObjectMapper()
    }

    fun setAA(ship: Ship)
    {
        ship.components.airDefense.forEach { (c, v) ->
            if (c.equals(ship.modules[airDefense], ignoreCase = true)) {
                processAura(ship, v.aaJoint)
            }
        }
        ship.components.atba.forEach { (c, v) ->
            if (c.equals(ship.modules[atba], ignoreCase = true)) {
                processAura(ship, v.aaJoint)
            }
        }
        ship.components.artillery.forEach { (c, v) ->
            if (c.equals(ship.modules[artillery], ignoreCase = true)) {
                processAura(ship, v.aaJoint)
            }
        }

        val auraFar = mutableListOf<Aura>()
        auraFar.addAll(ship.auraFar)

        ship.auraFar = sortAura(auraFar.filter { it.areaDamage > 0 }.toMutableList())
        ship.auraFarBubble = sortAura(auraFar.filter { it.bubbleDamage > 0 }.toMutableList())
        ship.auraMedium = sortAura(ship.auraMedium)
        ship.auraNear = sortAura(ship.auraNear)
    }

    private fun processAura(ship: Ship, aaJoint: AAJoint)
    {
        addAura(ship.auraFar, aaJoint.auraFar, false)
        addAura(ship.auraMedium, aaJoint.auraMedium, false)
        addAura(ship.auraNear, aaJoint.auraNear, false)
    }

    private fun addAura(aura: MutableList<Aura>, temp: MutableList<Aura>, hasBubble: Boolean)
    {
        for (x in temp) {
            if (!hasBubble || x.innerBubbleCount > 0 || x.outerBubbleCount > 0) {
                aura.add(x)
            }
        }
    }

    private fun sortAura(aura: MutableList<Aura>): MutableList<Aura>
    {
        if (aura.size > 1) {
            for (i in 1 until aura.size) {
                aura[0].innerBubbleCount = aura[0].innerBubbleCount + aura[i].innerBubbleCount
                aura[0].outerBubbleCount = aura[0].outerBubbleCount + aura[i].outerBubbleCount
                aura[0].areaDamage = aura[0].areaDamage + aura[i].areaDamage
            }
            aura.subList(1, aura.size).clear()
        }
        return aura
    }

    fun setParameters(ship: Ship)
    {
        for (i in ship.selectUpgrades.indices) {
            if (ship.selectUpgrades[i] > 0) {
                val modifier = mapper.convertValue(ship.upgrades[i][ship.selectUpgrades[i] - 1], CommonModifier::class.java)
                setUpgrades(ship, modifier)
            }
        }

        for (i in ship.selectSkills.indices) {
            if (ship.selectSkills[i] == 1) {
                val modifier = mapper.convertValue(ship.commander!!.crewSkills[skillGroup[ship.typeinfo.species]!![i / 6][i % 6].name], CommonModifier::class.java)
                setUpgrades(ship, modifier)

                val logicTrigger = mapper.convertValue(ship.commander!!.crewSkills[skillGroup[ship.typeinfo.species]!![i / 6][i % 6].name]!!.logicTrigger.modifiers, CommonModifier::class.java)
                setUpgrades(ship, logicTrigger)
            }
        }

        val flagsKey = flagsLHM.keys.toList()
        for (i in ship.selectFlags.indices) {
            if (ship.selectFlags[i] == 1) {
                val modifier = mapper.convertValue(flagsLHM[flagsKey[i]], CommonModifier::class.java)
                setUpgrades(ship, modifier)
            }
        }

        ship.consumables.forEach { slot ->
            slot.forEach { c ->
                c.subConsumables.forEach { (_, sub) ->
                    sub.bonus = getBonus(mapper.convertValue(sub, object : TypeReference<LinkedHashMap<String, Any>>() {}))
                }
            }
        }

        ship.components.fighter.forEach { (_, p) ->
            p.consumables.forEach { c ->
                c.subConsumables.forEach { (_, sVal) ->
                    sVal.bonus = getBonus(mapper.convertValue(sVal, object : TypeReference<LinkedHashMap<String, Any>>() {}))
                }
            }
        }

        ship.components.diveBomber.forEach { (_, p) ->
            p.consumables.forEach { c ->
                c.subConsumables.forEach { (_, sVal) ->
                    sVal.bonus = getBonus(mapper.convertValue(sVal, object : TypeReference<LinkedHashMap<String, Any>>() {}))
                }
            }
        }

        ship.components.skipBomber.forEach { (_, p) ->
            p.consumables.forEach { c ->
                c.subConsumables.forEach { (_, sVal) ->
                    sVal.bonus = getBonus(mapper.convertValue(sVal, object : TypeReference<LinkedHashMap<String, Any>>() {}))
                }
            }
        }

        ship.components.torpedoBomber.forEach { (_, p) ->
            p.consumables.forEach { c ->
                c.subConsumables.forEach { (_, sVal) ->
                    sVal.bonus = getBonus(mapper.convertValue(sVal, object : TypeReference<LinkedHashMap<String, Any>>() {}))
                }
            }
        }
    }

    private fun setUpgrades(ship: Ship, modifier: CommonModifier)
    {
        ship.components.artillery.forEach { (c, v) ->
            if (c.equals(ship.modules[artillery], ignoreCase = true)) {
//                v.GMIdealRadius = v.GMIdealRadius * modifier.gmidealRadius
                v.maxDist = v.maxDist * modifier.gmMaxDist * if (v.barrelDiameter > smallGun) oneCoeff else modifier.smallGunRangeCoefficient

                v.turrets.forEach { t ->
                    t.rotationSpeed[0] = t.rotationSpeed[0] * getShipTypeModifier(ship, modifier.gmRotationSpeed) +
                            if (t.barrelDiameter > smallGun) modifier.bigGunBonus else modifier.smallGunBonus
                    t.shotDelay = t.shotDelay * modifier.gmShotDelay *
                            (if (t.barrelDiameter > smallGun) oneCoeff else modifier.smallGunReloadCoefficient) *
                            (oneCoeff - ship.adrenaline * modifier.lastChanceReloadCoefficient)
                    t.idealRadiusModifier = t.idealRadiusModifier * modifier.gmIdealRadius
                }

                v.shells.forEach { (_, ammo) ->
                    if (HE.equals(ammo.ammoType, ignoreCase = true)) {
                        ammo.burnProb = ammo.burnProb + modifier.probabilityBonus + getShipTypeModifier(ship, modifier.artilleryBurnChanceBonus) +
                                (ammo.burnProbReal * (if (ammo.bulletDiametr > smallGun) modifier.chanceToSetOnFireBonusBig else modifier.chanceToSetOnFireBonusSmall)) +
                                (if (ammo.bulletDiametr > smallGunFlag) (modifier.burnChanceFactorBig - 1.0) else (modifier.burnChanceFactorSmall - 1.0)) +
                                (ammo.burnProbReal * if (ammo.bulletDiametr > smallGunFlag) (modifier.burnChanceFactorHighLevel - 1.0) else (modifier.burnChanceFactorLowLevel - 1.0))
                        ammo.alphaPiercingHE = ammo.alphaPiercingHE * modifier.penetrationCoeffHE *
                                if (ammo.bulletDiametr > smallGun) modifier.thresholdPenetrationCoefficientBig else modifier.thresholdPenetrationCoefficientSmall
                        ammo.alphaDamage = ammo.alphaDamage * modifier.gmHECSDamageCoeff
                    } else if (AP.equals(ammo.ammoType, ignoreCase = true)) {
                        ammo.alphaDamage = ammo.alphaDamage * modifier.gmAPDamageCoeff * getModifierByBarrelDiameter(ship, 0.190, modifier.gmHeavyCruiserCaliberDamageCoeff)
                    } else if (CS.equals(ammo.ammoType, ignoreCase = true)) {
                        ammo.alphaDamage = ammo.alphaDamage * modifier.gmHECSDamageCoeff
                    }
                }
            }
        }

        ship.components.torpedoes.forEach { (c, v) ->
            if (c.equals(ship.modules[torpedoes], ignoreCase = true)) {
                v.launchers.forEach { l: Launcher ->
                    l.rotationSpeed[0] = l.rotationSpeed[0] * modifier.gtRotationSpeed * getShipTypeModifier(ship, modifier.gmRotationSpeed)
                    l.shotDelay = l.shotDelay * modifier.gtShotDelay * modifier.launcherCoefficient * (oneCoeff - ship.adrenaline / modifier.hpStep * modifier.timeStep)
                }

                v.ammo.maxDist = v.ammo.maxDist * modifier.torpedoRangeCoefficient
                v.ammo.speed = (v.ammo.speed + modifier.torpedoSpeedBonus) * modifier.torpedoSpeedMultiplier
                v.ammo.uwCritical = v.ammo.uwCritical * modifier.floodChanceFactor
                v.ammo.visibilityFactor = v.ammo.visibilityFactor * modifier.torpedoVisibilityFactor
                v.ammo.alphaDamage = v.ammo.alphaDamage * modifier.torpedoDamageCoeff
                v.ammo.damage = v.ammo.damage * modifier.torpedoDamageCoeff
            }
        }

        ship.components.fighter.forEach { (c, v) ->
            if (c.equals(ship.modules[fighter], ignoreCase = true)) {
                setPlanes(ship, v, modifier)
                v.maxHealth = ((v.maxHealth + ship.level * modifier.planeHealthPerLevel) * modifier.fighterHealth * modifier.planeHealth).toInt()
                if (HE.equals(v.rocket.ammoType, ignoreCase = true)) {
                    v.rocket.burnProb = v.rocket.burnProb + modifier.rocketProbabilityBonus + (modifier.burnChanceFactorSmall - 1.0) + modifier.rocketBurnChanceBonus
                } else if (AP.equals(v.rocket.ammoType, ignoreCase = true)) {
                    v.rocket.alphaDamage = v.rocket.alphaDamage * modifier.rocketApAlphaDamageMultiplier
                }
            }
        }

        ship.components.diveBomber.forEach { (c, v) ->
            if (c.equals(ship.modules[diveBomber], ignoreCase = true)) {
                setPlanes(ship, v, modifier)
                v.maxHealth = ((v.maxHealth + ship.level * modifier.planeHealthPerLevel) * modifier.diveBomberHealth * modifier.planeHealth).toInt()
                if (HE.equals(v.bomb.ammoType, ignoreCase = true)) {
                    v.bomb.burnProb = v.bomb.burnProb + modifier.bombProbabilityBonus + (modifier.burnChanceFactorBig - 1.0) + modifier.bombBurnChanceBonus
                    v.bomb.alphaDamage = v.bomb.alphaDamage * modifier.bombAlphaDamageMultiplier
                } else if (AP.equals(v.bomb.ammoType, ignoreCase = true)) {
                    v.bomb.alphaDamage = v.bomb.alphaDamage * modifier.bombApAlphaDamageMultiplier
                }
                v.speedMoveWithBomb = v.speedMoveWithBomb * modifier.diveBomberSpeedMultiplier
                v.speedMax = v.speedMax * modifier.diveBomberMaxSpeedMultiplier
                v.speedMin = v.speedMin * modifier.diveBomberMinSpeedMultiplier
            }
        }

        ship.components.skipBomber.forEach { (c, v) ->
            if (c.equals(ship.modules[skipBomber], ignoreCase = true)) {
                setPlanes(ship, v, modifier)
                if (HE.equals(v.bomb.ammoType, ignoreCase = true)) {
                    v.bomb.burnProb = v.bomb.burnProb + modifier.bombProbabilityBonus + (modifier.burnChanceFactorBig - 1.0)
                    v.bomb.alphaDamage = v.bomb.alphaDamage * modifier.bombAlphaDamageMultiplier
                }
                v.speedMoveWithBomb = v.speedMoveWithBomb * modifier.skipBomberSpeedMultiplier
            }
        }

        ship.components.torpedoBomber.forEach { (c, v) ->
            if (c.equals(ship.modules[torpedoBomber], ignoreCase = true)) {
                setPlanes(ship, v, modifier)
                v.maxHealth = ((v.maxHealth + ship.level * modifier.planeHealthPerLevel) * modifier.torpedoBomberHealth * modifier.planeHealth).toInt()
                v.torpedo.maxDist = v.torpedo.maxDist * modifier.planeTorpedoRangeCoefficient
                v.torpedo.speed = v.torpedo.speed + modifier.planeTorpedoSpeedBonus
                v.torpedo.speed = v.torpedo.speed * modifier.planeTorpedoSpeedMultiplier
                v.torpedo.uwCritical = v.torpedo.uwCritical * modifier.floodChanceFactorPlane
            }
        }

        ship.components.airArmament.forEach { (c, v) ->
            if (c.equals(ship.modules[airArmament], ignoreCase = true)) {
                v.deckPlaceCount = (v.deckPlaceCount + modifier.planeExtraHangarSize).toInt()
            }
        }

        ship.components.atba.forEach { (c, v) ->
            if (c.equals(ship.modules[atba], ignoreCase = true)) {
//                v.GSIdealRadius = v.GSIdealRadius * modifier.gsidealRadius
                v.maxDist = v.maxDist * modifier.gsMaxDist * modifier.smallGunRangeCoefficient
                v.secondaries.forEach { (_, sec) ->
                    sec.shotDelay = sec.shotDelay * modifier.gsShotDelay * modifier.smallGunReloadCoefficient *
                            (oneCoeff - ship.adrenaline / modifier.hpStep * modifier.timeStep)
//                    sec.GSIdealRadius = sec.GSIdealRadius * modifier.gsidealRadius *
//                            if (ship.level >= 7) modifier.atbaIdealRadiusHi else modifier.atbaIdealRadiusLo
                    sec.idealRadiusModifier = sec.idealRadiusModifier * modifier.gsIdealRadius *
                            (if (ship.level >= 7) modifier.atbaIdealRadiusHi else modifier.atbaIdealRadiusLo) * modifier.gsPriorityTargetIdealRadius
                    if (HE.equals(sec.ammoType, ignoreCase = true)) {
                        sec.burnProb = sec.burnProb + modifier.probabilityBonus + (sec.burnProbReal * modifier.chanceToSetOnFireBonusSmall) + (modifier.burnChanceFactorSmall - 1.0)
                        sec.alphaPiercingHE = sec.alphaPiercingHE * modifier.thresholdPenetrationCoefficientSmall
                    }
                }
            }
        }

        ship.components.airDefense.forEach { (c, v) ->
            if (c.equals(ship.modules[airDefense], ignoreCase = true)) {
                v.prioritySectorStrength = v.prioritySectorStrength * modifier.prioritySectorStrengthCoefficient
                v.prioritySectorChangeDelay = v.prioritySectorChangeDelay * modifier.sectorSwitchDelayCoefficient
                v.prioritySectorEnableDelay = v.prioritySectorEnableDelay * modifier.sectorSwitchDelayCoefficient
                v.prioritySectorPreparation = v.prioritySectorPreparation * modifier.prioSectorCooldownCoefficient * modifier.prioritySectorCooldownMultiplier
                v.prioritySectorDuration = v.prioritySectorDuration * modifier.prioSectorCooldownCoefficient
                v.prioritySectorDamageInitial = v.prioritySectorDamageInitial * modifier.prioSectorStartPhaseStrengthCoefficient
                v.prioritySectorCoefficientDuring = v.prioritySectorCoefficientDuring + (modifier.prioritySectorStrengthBonus / 100.0)
            }
        }

        ship.components.hull.forEach { (c, v) ->
            if (c.equals(ship.modules[hull], ignoreCase = true)) {
                v.burnProb = v.burnProb * modifier.burnProb * modifier.probabilityCoefficient
                v.burnTime = v.burnTime * modifier.burnTime * modifier.critTimeCoefficient * modifier.hlCritTimeCoeff
                v.burnSizeSkill = if (modifier.fireResistanceEnabled) 3 else v.burnSizeSkill
                v.floodProb = v.floodProb * modifier.floodProb
                v.floodTime = v.floodTime * modifier.floodTime * modifier.critTimeCoefficient * modifier.hlCritTimeCoeff
                v.floodProtection = if (v.floodProtection > 0) v.floodProtection + modifier.uwCoeffBonus else v.floodProtection
                v.maxSpeed = v.maxSpeed * modifier.speedCoef
                v.rudderTime = v.rudderTime * modifier.sgRudderTime
                v.visibilityFactor = v.visibilityFactor * getShipTypeModifier(ship, modifier.visibilityDistCoeff) * modifier.visibilityFactor *
                        getModifierByBarrelDiameter(ship, 0.149, modifier.gmBigGunVisibilityCoeff)
                v.visibilityFactorByPlane = v.visibilityFactorByPlane * getShipTypeModifier(ship, modifier.visibilityDistCoeff) * modifier.visibilityFactorByPlane *
                        getModifierByBarrelDiameter(ship, 0.149, modifier.gmBigGunVisibilityCoeff)
                if (!excludeShipSpecies.contains(ship.typeinfo.species)) {
                    v.visibilityFactor = v.visibilityFactor * modifier.cruiserCoefficient
                    v.visibilityFactorByPlane = v.visibilityFactorByPlane * modifier.cruiserCoefficient
                }
                v.health = v.health + ship.level * modifier.healthPerLevel
            }
        }

        ship.components.engine.forEach { (c, v) ->
            if (c.equals(ship.modules[engine], ignoreCase = true)) {
                v.backwardEngineForsagMaxSpeed = v.backwardEngineForsagMaxSpeed * modifier.engineBackwardForsageMaxSpeed
                v.backwardEngineForsag = v.backwardEngineForsag * modifier.engineBackwardForsagePower
                v.backwardEngineUpTime = v.backwardEngineUpTime * modifier.engineBackwardUpTime
                v.forwardEngineForsagMaxSpeed = v.forwardEngineForsagMaxSpeed * modifier.engineForwardForsageMaxSpeed
                v.forwardEngineUpTime = v.forwardEngineUpTime * modifier.engineForwardUpTime
                v.forwardEngineForsag = v.forwardEngineForsag * modifier.engineForwardForsagePower
            }
        }

        ship.auraFar.forEach { aura -> setAura(aura, ship, modifier) }
        ship.auraFarBubble.forEach { aura -> setAura(aura, ship, modifier) }
        ship.auraMedium.forEach { aura -> setAura(aura, ship, modifier) }
        ship.auraNear.forEach { aura -> setAura(aura, ship, modifier) }
        ship.consumables.forEach { c ->
            c.forEach { s ->
                s.subConsumables.forEach { (_, sC) ->
                    if ("scout".equals(sC.consumableType, ignoreCase = true)) {
                        sC.workTime = sC.workTime * modifier.scoutWorkTime * modifier.scoutWorkTimeCoeff
                        sC.reloadTime = sC.reloadTime * modifier.scoutReloadCoeff
                        sC.numConsumables = sC.numConsumables + modifier.scoutAdditionalConsumables
                    } else if ("fighter".equals(sC.consumableType, ignoreCase = true)) {
                        sC.workTime = sC.workTime * modifier.fighterWorkTimeCoeff
                        sC.reloadTime = sC.reloadTime * modifier.fighterReloadCoeff
                    } else if ("artilleryBoosters".equals(sC.consumableType, ignoreCase = true)) {
                        sC.reloadTime = sC.reloadTime * modifier.artilleryBoostersReloadCoeff
                    } else if ("crashCrew".equals(sC.consumableType, ignoreCase = true)) {
                        sC.numConsumables = if (sC.numConsumables <= 0) sC.numConsumables else sC.numConsumables + modifier.crashCrewAdditionalConsumables
                        sC.workTime = sC.workTime * modifier.crashCrewWorkTime * modifier.crashCrewWorkTimeCoeff
                        if ("EmergencyTeamCooldownModifier".equals(modifier.modifier, ignoreCase = true)) {
                            sC.reloadTime = sC.reloadTime * modifier.reloadCoefficient
                        }
                        sC.reloadTime = sC.reloadTime * modifier.crashCrewReloadCoeff
                    } else if ("speedBoosters".equals(sC.consumableType, ignoreCase = true)) {
                        sC.workTime = sC.workTime * modifier.speedBoosterWorkTime * modifier.speedBoostersWorkTimeCoeff
                        sC.reloadTime = sC.reloadTime * modifier.speedBoostersReloadCoeff
                    } else if ("airDefenseDisp".equals(sC.consumableType, ignoreCase = true)) {
                        sC.workTime = sC.workTime * modifier.airDefenseDispWorkTime * modifier.airDefenseDispWorkTimeCoeff
                        sC.reloadTime = sC.reloadTime * modifier.airDefenseDispReloadMultiplier * modifier.airDefenseDispReloadCoeff
                    } else if ("sonar".equals(sC.consumableType, ignoreCase = true)) {
                        sC.workTime = sC.workTime * modifier.sonarSearchWorkTime * modifier.sonarWorkTimeCoeff
                        sC.reloadTime = sC.reloadTime * modifier.sonarReloadCoeff
                        if ("TorpedoAlertnessModifier".equals(modifier.modifier, ignoreCase = true)) {
                            sC.distTorpedo = sC.distTorpedo * modifier.rangeCoefficient
                        }
                    } else if ("rls".equals(sC.consumableType, ignoreCase = true)) {
                        sC.workTime = sC.workTime * modifier.rlsSearchWorkTime * modifier.rlsWorkTimeCoeff
                        sC.reloadTime = sC.reloadTime * modifier.rlsReloadCoeff
                    } else if ("smokeGenerator".equals(sC.consumableType, ignoreCase = true)) {
                        sC.workTime = sC.workTime * modifier.smokeGeneratorWorkTime * modifier.smokeGeneratorWorkTimeCoeff
                        sC.lifeTime = getDecimalRounded(sC.lifeTime * modifier.smokeGeneratorLifeTime, 3)
                        sC.radius = sC.radius * modifier.radiusCoefficient
                        sC.reloadTime = sC.reloadTime * modifier.smokeGeneratorReloadCoeff
                    } else if ("regenCrew".equals(sC.consumableType, ignoreCase = true)) {
                        sC.numConsumables = if (sC.numConsumables <= 0) sC.numConsumables else sC.numConsumables + modifier.regenCrewAdditionalConsumables
                        sC.workTime = sC.workTime * modifier.regenCrewWorkTimeCoeff
                        sC.regenerationHPSpeed = sC.regenerationHPSpeed * modifier.regenerationHPSpeed
                        sC.reloadTime = sC.reloadTime * modifier.regenCrewReloadCoeff
                    }

                    if ("AllSkillsCooldownModifier".equals(modifier.modifier, ignoreCase = true)) {
                        sC.reloadTime = sC.reloadTime * modifier.reloadCoefficient
                    }

                    if (sC.numConsumables > 0) {
                        sC.numConsumables = sC.numConsumables + modifier.additionalConsumables
                    }

                    sC.reloadTime = sC.reloadTime * modifier.consumableReloadTime
                    sC.workTime = sC.workTime * modifier.consumablesWorkTime
                }
            }
        }
    }

    private fun setAura(aura: Aura?, ship: Ship, modifier: CommonModifier)
    {
        if (aura != null) {
            if (aura.innerBubbleCount > 0) {
                aura.innerBubbleCount = aura.innerBubbleCount + modifier.aaExtraBubbles + modifier.aaInnerExtraBubbles
                aura.bubbleDamage = aura.bubbleDamage * modifier.aaOuterDamage * modifier.advancedOuterAuraDamageCoefficient *
                        getShipTypeModifier(ship, modifier.aaBubbleDamage)
            }
            aura.areaDamage = aura.areaDamage * modifier.aaNearDamage * modifier.nearAuraDamageCoefficient * getShipTypeModifier(ship, modifier.aaAuraDamage)
        }
    }

    private fun setPlanes(ship: Ship, plane: Plane, modifier: CommonModifier)
    {
        if (plane.hangarSettings != null) {
            plane.hangarSettings!!.timeToRestore = plane.hangarSettings!!.timeToRestore * modifier.planeSpawnTimeCoefficient * modifier.planeSpawnTime
            plane.hangarSettings!!.maxValue = plane.hangarSettings!!.maxValue + modifier.planeExtraHangarSize.toInt()

            plane.maxForsageAmount = plane.maxForsageAmount * modifier.forsageDurationCoefficient * modifier.planeForsageDuration
            plane.speedMoveWithBomb = plane.speedMoveWithBomb * modifier.flightSpeedCoefficient * modifier.planeSpeed *
                    (oneCoeff + ship.adrenaline / modifier.hpStep * modifier.squadronSpeedStep)
            plane.speedMove = plane.speedMove * modifier.flightSpeedCoefficient
            plane.speedMax = plane.speedMax * modifier.planeMaxSpeedMultiplier
            plane.speedMin = plane.speedMin * modifier.planeMinSpeedMultiplier
            plane.maxVisibilityFactor = plane.maxVisibilityFactor * modifier.squadronCoefficient *
                    modifier.squadronVisibilityDistCoeff * modifier.planeVisibilityFactor
            plane.maxVisibilityFactorByPlane = plane.maxVisibilityFactorByPlane * modifier.squadronCoefficient *
                    modifier.squadronVisibilityDistCoeff * modifier.planeVisibilityFactor
            plane.consumables.forEach { c ->
                c.subConsumables.forEach { (_, v) ->
                    if ("AllSkillsCooldownModifier".equals(modifier.modifier, ignoreCase = true)) {
                        v.reloadTime = v.reloadTime * modifier.reloadCoefficient
                    }
                    v.reloadTime = v.reloadTime * modifier.planeConsumableReloadTime
                    v.fightersNum = v.fightersNum + if (v.fightersNum > 0) modifier.extraFighterCount else 0.0

                    if ("regenerateHealth".equals(v.consumableType, ignoreCase = true)) {
                        v.regenerationRate = v.regenerationRate * modifier.planeRegenerationRate
                        v.numConsumables = v.numConsumables + modifier.regenerateHealthAdditionalConsumables
                        v.workTime = v.workTime * modifier.regenerateHealthWorkTimeCoeff
                    }
                    v.workTime = v.workTime * modifier.planeConsumablesWorkTime
                    v.numConsumables = v.numConsumables + modifier.planeAdditionalConsumables

                    if ("healForsage".equals(v.consumableType, ignoreCase = true)) {
                        v.reloadTime = v.reloadTime * modifier.healForsageReloadCoeff
                    }

                    if ("callFighters".equals(v.consumableType, ignoreCase = true)) {
                        v.radius = v.radius * modifier.callFightersRadiusCoeff
                        v.numConsumables = v.numConsumables + modifier.callFightersAdditionalConsumables
                        v.workTime = v.workTime * modifier.callFightersWorkTimeCoeff
                    }
                }
            }
        }
    }

    private fun getShipTypeModifier(ship: Ship, modifier: CommonModifierShip?): Double {
        return when (ship.typeinfo.species) {
            AIRCARRIER -> modifier?.aircraftCarrier ?: 1.0
            AUXILIARY -> modifier?.auxiliary ?: 1.0
            BATTLESHIP -> modifier?.battleship ?: 1.0
            CRUISER -> modifier?.cruiser ?: 1.0
            DESTROYER -> modifier?.destroyer ?: 1.0
            SUBMARINE -> modifier?.submarine ?: 1.0
            else -> 0.0
        }
    }

    private fun getModifierByBarrelDiameter(ship: Ship, barrelDiameter: Double, modifierValue: Double): Double {
        return ship.components.artillery[ship.modules[artillery]]?.turrets?.first()?.run {
            if (this.barrelDiameter >= barrelDiameter) modifierValue else 1.0
        } ?: 1.0
    }
}