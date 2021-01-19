@file:JvmName("Constant")

package WoWSFT.model.Constant

import java.math.BigDecimal

//object Constant
//{
    val distCoefWG = BigDecimal.valueOf(100.0 / 3.0)
    const val smallGun = 0.139
    const val smallGunFlag = 0.160
    const val oneCoeff = 1.0

    const val AP = "AP"
    const val HE = "HE"
    const val CS = "CS"
    const val EN = "en"
    const val IDS = "IDS"
    const val IDS_ = "IDS_"
    const val MODIFIER = "PARAMS_MODIFIER_"
    const val CONSUME = "DOCK_CONSUME_"
    const val TITLE = "TITLE_"
    const val DESCRIPTION = "DESCRIPTION_"
    const val DESC = "DESC_"

    const val TYPE_SHIP = "ships"
    const val TYPE_SHIP_LIST = "shipsList"
    const val TYPE_WARSHIP = "warship"
    const val TYPE_MODULE = "modules"
    const val TYPE_UPGRADE = "upgrades"
    const val TYPE_CONSUMABLE = "consumables"
    const val TYPE_COMMANDER = "commanders"
    const val TYPE_SKILL = "skills"
    const val TYPE_SHELL = "shells"
    const val TYPE_FLAG = "flags"
    const val TYPE_MISC = "misc"
    const val TYPE_PROJECTILE = "Projectile"
    const val TYPE_AIRCRAFT = "Aircraft"

    const val FILE_GAMEPARAMS = "live/GameParams.zip"
    const val FILE_JSON = ".json"
    const val FILE_SHIPS_ZIP = "files.zip"
    const val FILE_SHELLS_ZIP = "shells.zip"
    const val DIR_SHIPS = "ships/"
    const val DIR_SHELL = "shells/"

    const val flightControl = "flightControl"
    const val artillery = "artillery"
    const val hull = "hull"
    const val torpedoes = "torpedoes"
    const val suo = "suo"
    const val fighter = "fighter"
    const val torpedoBomber = "torpedoBomber"
    const val diveBomber = "diveBomber"
    const val skipBomber = "skipBomber"
    const val engine = "engine"

    const val airArmament = "airArmament"
    const val airDefense = "airDefense"
    const val atba = "atba"
    const val fireControl = "fireControl"

    const val hydrophone = "hydrophone"

    val miscList = listOf(TYPE_PROJECTILE, TYPE_AIRCRAFT)
    val componentsList = listOf(flightControl, artillery, hull, torpedoes, suo, fighter, torpedoBomber, diveBomber, skipBomber, engine)

    val globalLanguage = hashSetOf(EN)
    val excludeShipGroups = hashSetOf("unavailable", "disabled", "preserved", "clan")
    val supertestShipGroups = hashSetOf("demoWithoutStats")
    val researchShipGroups = hashSetOf("upgradeable", "start")
    val premiumShipGroups = hashSetOf("special", "specialUnsellable", "upgradeableExclusive", "ultimate", "upgradeableUltimate", "earlyAccess")
    val excludeShipNations = hashSetOf("Events", "disabled", "preserved", "clan")
    val excludeShipSpecies = hashSetOf("Auxiliary", "Submarine")
    val excludeCompStats = hashSetOf("directors", "finders", "radars")
    val excludeModernization = hashSetOf("extra", "aimingtime")
    val specialFlags = hashSetOf("PCEF028", "PCEF029")

    val coeff = hashSetOf("coef", "maxdist", "idealradius")
    val noUnit = hashSetOf("num")
    val extra = hashSetOf("count", "level", "additional")
    val meter = hashSetOf("radius", "height", "dist")
    val rate = hashSetOf("regeneration", "probabilitybonus", "chancebonus")
    val rateNoSym = hashSetOf("step")
    val multiple = hashSetOf("multiplier")
    val extraAngle = hashSetOf("gunbonus")
    val angle = hashSetOf("angle")
    val time = hashSetOf("time")
    val consumableTime = hashSetOf("workTime", "reloadTime", "lifeTime")
    val speed = hashSetOf("speedbonus")

    const val maxBitsToInt = 3766517952L

    const val GENERAL_INTERNAL_ERROR = "GENERAL_INTERNAL_ERROR"
    const val LOAD_FINISH = "loadFinish"
    const val SLASH = "/"
    const val NOTIFICATION = "notification"
    const val GLOBAL = "global"
    const val JSON_PARSER = "jsonParser"
    const val CUSTOM_SKIlL_GROUP = "customSkillGroup"

    const val CDN_IMAGE = "https://cdn.wowsft.com/images"
    const val CDN = "https://cdn.wowsft.com/"

    const val AIRCARRIER = "AirCarrier"
    const val AUXILIARY = "Auxiliary"
    const val BATTLESHIP = "Battleship"
    const val CRUISER = "Cruiser"
    const val DESTROYER = "Destroyer"
    const val SUBMARINE = "Submarine"
//}