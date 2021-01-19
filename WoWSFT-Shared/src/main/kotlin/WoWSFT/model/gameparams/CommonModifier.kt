package WoWSFT.model.gameparams

import WoWSFT.config.WoWSFT
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@WoWSFT
@JsonIgnoreProperties(ignoreUnknown = true)
open class CommonModifier
{
    @JsonProperty("AAExtraBubbles")
    var aaExtraBubbles = 0.0
    @JsonProperty("AANearDamage")
    var aaNearDamage = 1.0
    @JsonProperty("AAOuterDamage")
    var aaOuterDamage = 1.0
    @JsonProperty("ADMaxHP")
    var adMaxHP = 1.0
    @JsonProperty("GMCritProb")
    var gmCritProb = 1.0
    @JsonProperty("GMIdealRadius")
    var gmIdealRadius = 1.0
    @JsonProperty("GMMaxDist")
    var gmMaxDist = 1.0
    @JsonProperty("GMMaxHP")
    var gmMaxHP = 1.0
    @JsonProperty("GMRepairTime")
    var gmRepairTime = 1.0
    @JsonProperty("GMRotationSpeed")
    var gmRotationSpeed: CommonModifierShip? = null
    @JsonProperty("GMShotDelay")
    var gmShotDelay = 1.0
    @JsonProperty("GMSigmaCount")
    var gmSigmaCount = 1.0
    @JsonProperty("GSIdealRadius")
    var gsIdealRadius = 1.0
    @JsonProperty("GSMaxDist")
    var gsMaxDist = 1.0
    @JsonProperty("GSMaxHP")
    var gsMaxHP = 1.0
    @JsonProperty("GSShotDelay")
    var gsShotDelay = 1.0
    @JsonProperty("GSSigmaCount")
    var gsSigmaCount = 1.0
    @JsonProperty("GTCritProb")
    var gtCritProb = 1.0
    @JsonProperty("GTMaxHP")
    var gtMaxHP = 1.0
    @JsonProperty("GTRepairTime")
    var gtRepairTime = 1.0
    @JsonProperty("GTRotationSpeed")
    var gtRotationSpeed = 1.0
    @JsonProperty("GTShotDelay")
    var gtShotDelay = 1.0
    @JsonProperty("PMCritProb")
    var pmCritProb = 1.0
    @JsonProperty("PMDetonationProb")
    var pmDetonationProb = 1.0
    @JsonProperty("PMRepairTime")
    var pmRepairTime = 1.0
    @JsonProperty("SGCritProb")
    var sgCritProb = 1.0
    @JsonProperty("SGRepairTime")
    var sgRepairTime = 1.0
    @JsonProperty("SGRudderTime")
    var sgRudderTime = 1.0
    var airDefenseDispWorkTime = 1.0
    var burnProb = 1.0
    var burnTime = 1.0
    var crashCrewWorkTime = 1.0
    var engineBackwardForsageMaxSpeed = 1.0
    var engineBackwardForsagePower = 1.0
    var engineBackwardUpTime = 1.0
    var engineCritProb = 1.0
    var engineForwardForsageMaxSpeed = 1.0
    var engineForwardForsagePower = 1.0
    var engineForwardUpTime = 1.0
    var engineRepairTime = 1.0
    var floodProb = 1.0
    var floodTime = 1.0
    var invulnerableWorkTime = 1.0
    var rlsSearchWorkTime = 1.0
    var scoutWorkTime = 1.0
    var shootShift = 1.0
    var smokeGeneratorLifeTime = 1.0
    var smokeGeneratorWorkTime = 1.0
    var sonarSearchWorkTime = 1.0
    var speedBoosterWorkTime = 1.0
    var squadronVisibilityDistCoeff = 1.0
    var trigger1SearchWorkTime = 1.0
    var trigger2SearchWorkTime = 1.0
    var trigger3SearchWorkTime = 1.0
    var trigger4SearchWorkTime = 1.0
    var trigger5SearchWorkTime = 1.0
    var trigger6SearchWorkTime = 1.0
    var visibilityDistCoeff: CommonModifierShip? = null
    var visionDistCoeff = 1.0
    var visionTorpedoCoeff = 1.0
    var visionXRayShipCoeff = 1.0
    //
    var modifier = ""
    var diveBomber = 1.0
    var fighter = 1.0
    var torpedoBomber = 1.0
    var radiusCoefficient = 1.0
    var smallGunReloadCoefficient = 1.0
    var smallGunRangeCoefficient = 1.0
    var consumablePlaneSpeedCoefficient = 1.0
    var reloadCoefficient = 1.0
    var alertMinDistance = 0.0
    var critTimeCoefficient = 1.0
    var atbaIdealRadiusHi = 1.0
    var atbaIdealRadiusLo = 1.0
    var fightersAmmunitionCoefficient = 1.0
    var fightersEfficiencyCoefficient = 1.0
    var probabilityBonus = 0.0
    var probabilityCoefficient = 1.0
    var switchAmmoReloadCoef = 1.0
    var chanceToSetOnFireBonus = 0.0
    var chanceToSetOnFireBonusBig = 0.0
        set(value) { field = if (value != 0.0) -value else 0.0 }
    var chanceToSetOnFireBonusSmall = 0.0
        set(value) { field = if (value != 0.0) -value else 0.0 }
    var thresholdPenetrationCoefficient = 1.0
    var thresholdPenetrationCoefficientBig = 1.0
    var thresholdPenetrationCoefficientSmall = 1.0
    var hpStep = 1.0
    var timeStep = 0.0
    var critRudderTimeCoefficient = 1.0
    var bigGunBonus = 0.0
    var smallGunBonus = 0.0
    var critProbCoefficient = 1.0
    var fightersSpeedCoefficient = 1.0
    var fightersVisibCoefficient = 1.0
    var fightersVitalCoefficient = 1.0
    var burnTimePenalty = 0.0
    var diveBombersPrepareCoefficient = 1.0
    var fightersPrepareCoefficient = 1.0
    var torpedoBombersPrepareCoefficient = 1.0
    var vitalityCoefficient = 1.0
    var fightersPassiveEfficiencyCoefficient = 1.0
    var additionalConsumables = 0
    var healthPerLevel = 0.0
    var torpedoRangeCoefficient = 1.0
    var torpedoSpeedBonus = 0.0
    var rangeCoefficient = 1.0
    var bomberCoefficient = 1.0
    var launcherCoefficient = 1.0
    var aircraftCarrierCoefficient = 1.0
    var battleshipCoefficient = 1.0
    var cruiserCoefficient = 1.0
    var destroyerCoefficient = 1.0
    var submarineCoefficient = 1.0
    var squadronCoefficient = 1.0
    var nearAuraDamageCoefficient = 1.0
    var advancedOuterAuraDamageCoefficient = 1.0
    var extraFighterCount = 0.0
    var fighterLifeTimeCoefficient = 1.0
    var hangarSizeBonus = 0.0
    var planeSpawnTimeCoefficient = 1.0
    var prioritySectorStrengthCoefficient = 1.0
    var sectorSwitchDelayCoefficient = 1.0
    var bombProbabilityBonus = 0.0
    var rocketProbabilityBonus = 0.0
    var flightSpeedCoefficient = 1.0
    var forsageDurationCoefficient = 1.0
    var squadronHealthStep = 1.0
    var squadronSpeedStep = 0.0
    var nearAuraDamageTakenCoefficient = 1.0
    var planeHealthPerLevel = 0.0
    var planeTorpedoRangeCoefficient = 1.0
    var planeTorpedoSpeedBonus = 0.0
    var planeRangeCoefficient = 1.0
    var prioSectorCooldownCoefficient = 1.0
    var prioSectorPhaseDurationCoefficient = 1.0
    var prioSectorStartPhaseStrengthCoefficient = 1.0
    var prioSectorStrengthCoefficient = 1.0

    var burnChanceFactorBig = 1.0
    var burnChanceFactorSmall = 1.0
    var collisionDamageApply = 1.0
    var collisionDamageNerf = 1.0
    var damageCoeffGM = 1.0
    var damageCoeffTorpedo = 1.0
    var floodChanceFactor = 1.0
    var floodChanceFactorPlane = 1.0
    var regenerationHPSpeed = 1.0
    var speedCoef= 1.0

    var torpedoSpeedMultiplier = 1.0
    @JsonProperty("InnerAADefenseDispExtraBubbles")
    var innerAADefenseDispExtraBubbles = 0.0
    var airDefenseDispReloadMultiplier = 1.0
    var prioritySectorCooldownMultiplier = 1.0
    var guaranteedTorpedoVisibilityDistance = 0.0

    var afterBattleRepair = 1.0
    var artilleryKruppMultiplier = 1.0
    var bombAlphaDamageMultiplier = 1.0
    var bombKruppMultiplier = 1.0
    var canCharge = false
    var canSell = false
    var costCR = 0
    var costGold = 0
    var creditsFactor =  1.0
    var crewExpFactor = 1.0
    var diveBomberAccuracyIncRateCoeff = 1.0
    var diveBomberMaxSpeedMultiplier = 1.0
    var diveBomberMinSpeedMultiplier = 1.0
    var diveBomberSpeedMultiplier = 1.0
    var diveBomberSpreadMultiplier = 1.0
    var expFactor = 1.0
    var fighterAccuracyIncRateCoeff = 1.0
    var fighterMaxSpeedMultiplier = 1.0
    var fighterMinSpeedMultiplier = 1.0
    var fighterSpeedMultiplier = 1.0
    var fighterSpreadMultiplier = 1.0
    var freeExpFactor = 1.0
    var hullHealthCoeff = 1.0
    var planeAdditionalConsumables = 0
    var planeMaxSpeedMultiplier = 1.0
    var planeMinSpeedMultiplier = 1.0
    var receiveGMDamageCoeff = 1.0
    var receiveTorpedoDamageCoeff = 1.0
    var rocketAlphaDamageMultiplier = 1.0
    var rocketKruppMultiplier = 1.0
    var tags = emptyList<Any>()
    var torpedoBomberAccuracyIncRateCoeff = 1.0
    var torpedoBomberMaxSpeedMultiplier = 1.0
    var torpedoBomberMinSpeedMultiplier = 1.0
    var torpedoBomberSpeedMultiplier = 1.0
    var torpedoDamageCoeff = 1.0
    var torpedoVisibilityFactorCoeff = 1.0
    var uwCoeffBonus = 0.0
    var uwCoeffMultiplier = 1.0
    var visibilityFactor = 1.0
    var visibilityFactorByPlane = 1.0
    var visibilityFactorInSmoke = 1.0

    @JsonProperty("AAInnerExtraBubbles")
    var aaInnerExtraBubbles = 0
    @JsonProperty("AAMaxHP")
    var aaMaxHP = 1.0
    @JsonProperty("ConsumableReloadTime")
    var consumableReloadTime = 1.0
    @JsonProperty("ConsumablesWorkTime")
    var consumablesWorkTime = 1.0
    @JsonProperty("SGRudderPower")
    var sgRudderPower = 1.0
    var artilleryDamageCoef = 1.0
    var burnChanceMultiplier = 1.0
    var diveBomberHealth = 1.0
    var engineForsageCoef = 1.0
    var fighterAimingTimeZone =  1.0
    var fighterAimingTime = 0.0
    var fighterHealth = 1.0
    var healthHullCoeff = 1.0
    var healthRegen = 0.0
    var healthRegenPercent = 0.0
    var planeConsumableReloadTime = 1.0
    var planeConsumablesWorkTime = 1.0
    var planeEmptyReturnSpeed = 1.0
    var planeExtraHangarSize = 0.0
    var planeForsageDuration = 1.0
    var planeForsageRegeneration = 1.0
    var planeHealth = 1.0
    var planeHealthRegenPercent = 0.0
    var planeRegenerationRate = 1.0
    var planeSpawnTime = 1.0
    var planeSpeed = 1.0
    var planeTorpedoSpeedMultiplier = 1.0
    var planeVisibilityFactor = 1.0
    var receiveArtilleryDamageCoeff = 1.0
    var torpedoBomberAimingTime = 0.0
    var torpedoBomberHealth = 1.0
    var torpedoVisibilityFactor = 1.0
    var visionXRayTorpedoDist = 0.0

    @JsonProperty("ConsumableReloadTimeSlot0")
    var consumableReloadTimeSlot0 = 1.0
    @JsonProperty("ConsumableReloadTimeSlot1")
    var consumableReloadTimeSlot1 = 1.0
    @JsonProperty("ConsumableReloadTimeSlot2")
    var consumableReloadTimeSlot2 = 1.0
    @JsonProperty("ConsumableReloadTimeSlot3")
    var consumableReloadTimeSlot3 = 1.0
    @JsonProperty("ConsumableReloadTimeSlot4")
    var consumableReloadTimeSlot4 = 1.0
    @JsonProperty("ConsumablesWorkTimeSlot0")
    var consumablesWorkTimeSlot0 = 1.0
    @JsonProperty("ConsumablesWorkTimeSlot1")
    var consumablesWorkTimeSlot1 = 1.0
    @JsonProperty("ConsumablesWorkTimeSlot2")
    var consumablesWorkTimeSlot2 = 1.0
    @JsonProperty("ConsumablesWorkTimeSlot3")
    var consumablesWorkTimeSlot3 = 1.0
    @JsonProperty("ConsumablesWorkTimeSlot4")
    var consumablesWorkTimeSlot4 = 1.0
    @JsonProperty("GLAlphaFactor")
    var glAlphaFactor = 1.0
    @JsonProperty("GLMaxDist")
    var glMaxDist = 1.0
    @JsonProperty("GLRotationSpeed")
    var glRotationSpeed = 1.0
    @JsonProperty("GLShotDelay")
    var glShotDelay = 1.0
    @JsonProperty("GMAlphaFactor")
    var gmAlphaFactor = 1.0
    @JsonProperty("GSAlphaFactor")
    var gsAlphaFactor = 1.0
    @JsonProperty("GWAlphaFactor")
    var gwAlphaFactor = 1.0
    @JsonProperty("GWMaxDist")
    var gwMaxDist = 1.0
    @JsonProperty("GWRotationSpeed")
    var gwRotationSpeed = 1.0
    @JsonProperty("GWShotDelay")
    var gwShotDelay = 1.0
    var buffsShiftMaxLevel = 0
    var buffsShiftSpeed = 1.0
    var buffsStartPool = 0
    var damageAbsorbChance = 0.0
    var damageHealCoef = 0.0
    var damageResistCoef = 1.0
    var extraDamageChance = 0.0
    var extraDamageFactor = 1.0
    var waveSpeedMultiplier = 1.0

    @JsonProperty("AAAuraDamage")
    var aaAuraDamage: CommonModifierShip? = null
    @JsonProperty("AAAuraReceiveDamageCoeff")
    var aaAuraReceiveDamageCoeff = 1.0
    @JsonProperty("AABubbleDamage")
    var aaBubbleDamage: CommonModifierShip? = null
    var artilleryBurnChanceBonus = CommonModifierShip(0.0)
    var prioritySectorStrengthBonus = 0
    @JsonProperty("GMAPDamageCoeff")
    var gmAPDamageCoeff = 1.0
    @JsonProperty("GMHeavyCruiserCaliberDamageCoeff")
    var gmHeavyCruiserCaliberDamageCoeff = 1.0
    var lastChanceReloadCoefficient = 0.0
    @JsonProperty("GSPriorityTargetIdealRadius")
    var gsPriorityTargetIdealRadius = 1.0
    var crashCrewAdditionalConsumables = 0
    var crashCrewReloadCoeff = 1.0
    var crashCrewWorkTimeCoeff = 1.0
    var regenCrewAdditionalConsumables = 0
    var regenCrewReloadCoeff = 1.0
    var regenCrewWorkTimeCoeff = 1.0
    var rlsWorkTimeCoeff = 1.0
    var smokeGeneratorWorkTimeCoeff = 1.0
    var sonarWorkTimeCoeff = 1.0
    var speedBoostersWorkTimeCoeff = 1.0
    var airDefenseDispReloadCoeff = 1.0
    var artilleryBoostersReloadCoeff = 1.0
    var fighterReloadCoeff = 1.0
    var scoutAdditionalConsumables = 0
    var scoutReloadCoeff = 1.0
    var scoutWorkTimeCoeff = 1.0
    var torpedoReloaderReloadCoeff = 1.0
    var hlCritTimeCoeff = 1.0
    var fireResistanceEnabled = false
    var priorityTargetEnabled = false
    var artilleryAlertEnabled = false
    var artilleryAlertMinDistance = 0.0
    var nearEnemyIntuitionEnabled = false
    var torpedoDetectionCoefficient = 1.0
    var torpedoDetectionCoefficientByPlane = 1.0
    var bombBurnChanceBonus = 0.0
    var rocketBurnChanceBonus = 0.0
    var burnChanceFactorHighLevel = 1.0
    var burnChanceFactorLowLevel = 1.0
    var penetrationCoeffHE = 1.0
    @JsonProperty("GMBigGunVisibilityCoeff")
    var gmBigGunVisibilityCoeff = 1.0
    @JsonProperty("GMHECSDamageCoeff")
    var gmHECSDamageCoeff = 1.0
    @JsonProperty("SGCritRudderTime")
    var sgCritRudderTime = 1.0
    var softCriticalEnabled = false
    var damagedEngineCoeff = 1.0
    var skipBomberAccuracyIncRateCoeff = 1.0
    var bombApAlphaDamageMultiplier = 1.0
    var rocketApAlphaDamageMultiplier = 1.0
    var callFightersAdditionalConsumables = 0
    var callFightersAppearDelay = 1.0
    var callFightersTimeDelayAttack = 1.0
    var callFightersWorkTimeCoeff = 1.0
    var callFightersRadiusCoeff = 1.0
    var callFightersAirOnly = false
    var consumableDescID = ""
    var consumableIconID = ""
    var consumableTitleID = ""
    var regenerateHealthAdditionalConsumables = 0
    var regenerateHealthWorkTimeCoeff = 1.0
    var healForsageReloadCoeff = 1.0
    var planeBubbleArmorCoeff = 1.0
    var skipBomberSpeedMultiplier = 1.0
    var planeForsageDrainRate = 1.0
    var restoreForsage = false
    var planeTorpedoArmingTimeCoeff = 1.0
    var ignorePTZBonus = 0
    @JsonProperty("GMDamageCoeff")
    var gmDamageCoeff = 1.0
    @JsonProperty("GMRotationSpeedBonus")
    var gmRotationSpeedBonus = 0.0
    @JsonProperty("GSIdealRadiusHighLevel")
    var gsIdealRadiusHighLevel = 1.0
    @JsonProperty("GSIdealRadiusLowLevel")
    var gsIdealRadiusLowLevel = 1.0
    @JsonProperty("GSRotationSpeed")
    var gsRotationSpeed = 1.0
    @JsonProperty("GSRotationSpeedBonus")
    var gsRotationSpeedBonus = 0.0
    @JsonProperty("GTRotationSpeedBonus")
    var gtRotationSpeedBonus = 0.0
    @JsonProperty("GWRotationSpeedBonus")
    var gwRotationSpeedBonus = 0.0
    var airDefenseDispWorkTimeCoeff = 1.0
    var allConsumableReloadTime = 1.0
    var fighterWorkTimeCoeff = 1.0
    var lastChanceSquadronSpeedCoefficient = 0.0
    var prioritySectorPhaseDurationCoefficient = 1.0
    var prioritySectorStartPhaseStrengthCoefficient = 1.0
    var regenerateHealthReloadCoeff = 1.0
    var rlsReloadCoeff = 1.0
    var skipBomberHealth = 1.0
    var skipBomberMaxSpeedMultiplier = 1.0
    var skipBomberMinSpeedMultiplier = 1.0
    var smokeGeneratorReloadCoeff = 1.0
    var smokeScreenRadiusCoefficient = 1.0
    var sonarReloadCoeff = 1.0
    var speedBoostersReloadCoeff = 1.0
}