package WoWSFT.parser

import WoWSFT.model.Constant.*
import WoWSFT.model.gameparams.TypeInfo
import WoWSFT.model.gameparams.commander.*
import WoWSFT.model.gameparams.consumable.*
import WoWSFT.model.gameparams.flag.Flag
import WoWSFT.model.gameparams.modernization.*
import WoWSFT.model.gameparams.ship.*
import WoWSFT.model.gameparams.ship.component.airarmament.AirArmament
import WoWSFT.model.gameparams.ship.component.airdefense.AirDefense
import WoWSFT.model.gameparams.ship.component.artillery.*
import WoWSFT.model.gameparams.ship.component.atba.ATBA
import WoWSFT.model.gameparams.ship.component.engine.Engine
import WoWSFT.model.gameparams.ship.component.firecontrol.FireControl
import WoWSFT.model.gameparams.ship.component.flightcontrol.FlightControl
import WoWSFT.model.gameparams.ship.component.hull.Hull
import WoWSFT.model.gameparams.ship.component.torpedo.Torpedo
import WoWSFT.model.gameparams.ship.upgrades.*
import WoWSFT.service.ParamService
import WoWSFT.utils.CommonUtils
import WoWSFT.utils.PenetrationUtils
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class JsonParser
{
    @Autowired private lateinit var paramService: ParamService
    @Autowired private lateinit var executor: ThreadPoolTaskExecutor

    private val nameToId = HashMap<String, String>()
    private val idToName = HashMap<String, String>()
    private val gameParamsHM = HashMap<String, Any>()
    private val global = HashMap<String, HashMap<String, Any>>()
    private val ships = LinkedHashMap<String, Ship>()
    private val consumables = LinkedHashMap<String, Consumable>()
    private val commanders = LinkedHashMap<String, Commander>()
    private val upgrades = LinkedHashMap<Int, LinkedHashMap<String, Modernization>>()
    private val shipsList = LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<Int, MutableList<ShipIndex>>>>>()
    private val flags = LinkedHashMap<String, Flag>()
    private val shells = HashMap<String, Shell>()
    private val misc = HashMap<String, Any>()
    private val penetrationUtils = PenetrationUtils()

    companion object {
        private val log = LoggerFactory.getLogger(JsonParser::class.java)
        private val mapper = ObjectMapper().registerKotlinModule()
        private val job = SupervisorJob()
    }

    init {
        val indenter: Indenter = DefaultIndenter("    ", DefaultIndenter.SYS_LF)
        val printer = DefaultPrettyPrinter()
        printer.indentObjectsWith(indenter)
        printer.indentArraysWith(indenter)
        mapper.setDefaultPrettyPrinter(printer)
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.setAnnotationIntrospector(IgnoreJacksonWriteOnlyAccess())

        repeat(6) { upgrades[it] = LinkedHashMap() }
    }

    @Throws(IOException::class)
    fun setGlobal()
    {
        log.info("Setting up Global")

        globalLanguage.forEach {
            val globalFile = ClassPathResource("/json/live/global-$it$FILE_JSON")
            global[it] = mapper.readValue(globalFile.inputStream, object: TypeReference<HashMap<String, Any>>() {})
        }
    }

    @Throws(IOException::class)
    fun setGameParams()
    {
        log.info("Setting up GameParams")

        val zf = ZipFile(ClassPathResource("/json/live/GameParams.zip").file.path)
        val temp = mapper.readValue(zf.getInputStream(zf.entries().nextElement()), object: TypeReference<LinkedHashMap<String, LinkedHashMap<String, Any>>>() {})

        temp.forEach { (key, value) ->
            val typeInfo = mapper.convertValue(value["typeinfo"], TypeInfo::class.java)
            if (typeInfo.type.equals("Ship", ignoreCase = true) && !excludeShipNations.contains(typeInfo.nation) && !excludeShipSpecies.contains(typeInfo.species)) {
                val ship = mapper.convertValue(value, Ship::class.java)
                if (!excludeShipGroups.contains(ship.group)) {
                    ship.shipUpgradeInfo.components.forEach { (cType, c) ->
                        c.forEach { su ->
                            excludeCompStats.forEach { su.components.remove(it) }
                            su.elem = componentsList.indexOf(cType)
                        }
                    }
                    addShips(ship)
                }
            } else if (typeInfo.type.equals("Modernization", ignoreCase = true)) {
                val modernization = mapper.convertValue(value, Modernization::class.java)
                if (modernization.slot >= 0) {
                    paramService.setBonusParams(key, mapper.convertValue(modernization, jacksonTypeRef<LinkedHashMap<String, Any>>()), modernization.bonus, false)
                    upgrades[modernization.slot]!![modernization.name] = modernization
                }
            } else if (typeInfo.type.equals("Ability", ignoreCase = true) && !excludeShipNations.contains(typeInfo.nation) && !key.contains("Super")) {
                val consumable = mapper.convertValue(value, Consumable::class.java)
                consumables[key] = consumable
            } else if (typeInfo.type.equals("Crew", ignoreCase = true)) {
                val commander = mapper.convertValue(value, Commander::class.java)
                if (!"Events".equals(commander.typeinfo.nation, ignoreCase = true)) {
                    if (!commander.crewPersonality.unique && commander.typeinfo.nation == "Common") {
                        commander.identifier = "IDS_CREW_LASTNAME_DEFAULT"
                        commanders[commander.index.toUpperCase()] = commander
                    } else if (commander.crewPersonality.unique) {
                        commander.identifier = "$IDS_${commander.crewPersonality.personName.toUpperCase()}"
                        commanders[commander.index.toUpperCase()] = commander
                    }
                }
            } else if (typeInfo.type.equals("Exterior", ignoreCase = true) && typeInfo.species.equals("Flags", ignoreCase = true)) {
                val flag = mapper.convertValue(value, Flag::class.java)
                if (flag.group == 0) {
                    flag.identifier = "$IDS_${flag.name.toUpperCase()}"
                    paramService.setBonusParams(key, mapper.convertValue(flag, object: TypeReference<LinkedHashMap<String, Any>>() {}), flag.bonus, specialFlags.any { flag.index == it })
                    flags[flag.name] = flag
                }
            } else if (miscList.contains(typeInfo.type)) {
                if ("Artillery" == typeInfo.species) {
                    val shell = mapper.convertValue(value, Shell::class.java)
                    if (AP == shell.ammoType) {
                        shells[key] = shell
                    }
                }
                misc[key] = value
            } else {
                gameParamsHM[key] = value
            }
        }

        generateShipsList()
        sortUpgrades()
        setCommanderParams()
        sortFlags()

        temp.clear()
    }

    private fun addShips(ship: Ship)
    {
        sortShipUpgradeInfo(ship)
        setRealShipType(ship)
        setRows(ship)
        ships[ship.index] = ship
        idToName[ship.name] = ship.index
        try {
            nameToId[global[EN]!!["$IDS_${ship.index.toUpperCase()}_FULL"].toString()] = ship.index.toUpperCase()
        } catch (npe: NullPointerException) {
            log.info(ship.index.toUpperCase())
        }
    }

    private fun sortShipUpgradeInfo(ship: Ship)
    {
        ship.shipUpgradeInfo.components.forEach { (key, value) ->
            value.forEach { upgrade ->
                if (upgrade.position == 3 && ship.shipUpgradeInfo.components[key]!!.size < 3) {
                    upgrade.position = 2
                }

                upgrade.components.forEach { (cKey, cValue) ->
                    when (cKey.decapitalize()) {
                        artillery -> cValue.forEach { cVal -> ship.components.artillery[cVal] = mapper.convertValue(ship.tempComponents[cVal], Artillery::class.java) }
                        airDefense -> cValue.forEach { cVal -> ship.components.airDefense[cVal] = mapper.convertValue(ship.tempComponents[cVal], AirDefense::class.java) }
                        atba -> cValue.forEach { cVal -> ship.components.atba[cVal] = mapper.convertValue(ship.tempComponents[cVal], ATBA::class.java) }
                        engine -> cValue.forEach { cVal -> ship.components.engine[cVal] = mapper.convertValue(ship.tempComponents[cVal], Engine::class.java) }
                        suo -> cValue.forEach { cVal -> ship.components.suo[cVal] = mapper.convertValue(ship.tempComponents[cVal], FireControl::class.java) }
                        hull -> cValue.forEach { cVal -> ship.components.hull[cVal] = mapper.convertValue(ship.tempComponents[cVal], Hull::class.java) }
                        torpedoes -> cValue.forEach { cVal -> ship.components.torpedoes[cVal] = mapper.convertValue(ship.tempComponents[cVal], Torpedo::class.java) }
                        airArmament -> cValue.forEach { cVal -> ship.components.airArmament[cVal] = mapper.convertValue(ship.tempComponents[cVal], AirArmament::class.java) }
                        flightControl -> cValue.forEach { cVal -> ship.components.flightControl[cVal] = mapper.convertValue(ship.tempComponents[cVal], FlightControl::class.java) }
                        fighter, diveBomber, torpedoBomber, skipBomber -> cValue.forEach { cVal ->
                            val tempPlaneType = mapper.convertValue(ship.tempComponents[cVal], object: TypeReference<HashMap<String, String>>() {})
                            ship.planes[cVal] = tempPlaneType["planeType"]!!
                        }
                    }
                }
            }
            value.sortBy { it.position }
        }

        ship.shipUpgradeInfo.components.forEach { (_, value) ->
            value.forEach { upgrade ->
                if (upgrade.prev.isNotEmpty()) {
                    for ((_, value1) in ship.shipUpgradeInfo.components) {
                        val tSU = value1.stream().filter { v -> v.name.equals(upgrade.prev, ignoreCase = true) }.findFirst().orElse(null)
                        if (tSU != null) {
                            upgrade.prevType = tSU.ucTypeShort
                            upgrade.prevPosition = tSU.position
                            upgrade.prevElem = componentsList.indexOf(tSU.ucTypeShort)
                            if (upgrade.position == upgrade.prevPosition && upgrade.ucTypeShort.equals(upgrade.prevType, ignoreCase = true)) {
                                upgrade.position = upgrade.prevPosition + 1
                            }
                            break
                        }
                    }
                }
            }
            value.sortBy { it.position }
        }
        ship.tempComponents = LinkedHashMap()
    }

    private fun setRealShipType(ship: Ship)
    {
        when {
            researchShipGroups.contains(ship.group) -> {
                ship.realShipType = ship.typeinfo.species!!
                ship.research = true
            }
            premiumShipGroups.contains(ship.group) -> { ship.realShipType = "Premium" }
            supertestShipGroups.contains(ship.group) -> { ship.realShipType = "Test_Sample" }
        }
    }

    private fun setRows(ship: Ship)
    {
        val colCount = LinkedHashMap<String, Int>()
        var maxRows = 0
        for (i in 1..3) {
            var hasRow = false
            ship.shipUpgradeInfo.components.forEach { (key, compList) ->
                if (key == flightControl) { return@forEach }

                colCount.putIfAbsent(key, 0)
                val posCount = compList.filter { it.position == i }.size
                if (posCount > 0) {
                    hasRow = true
                }
                if (colCount[key]!! < posCount) {
                    colCount[key] = posCount
                }
            }
            if (hasRow) {
                maxRows++
            }
        }
        ship.shipUpgradeInfo.also {
            it.cols = colCount
            it.maxRows = maxRows
        }
    }

    private fun generateShipsList()
    {
        ships.forEach { (_, ship) ->
            ship.shipUpgradeInfo.components.forEach { (_, components) ->
                components.forEach { component ->
                    if (component.nextShips.isNotEmpty()) {
                        component.nextShips.forEach { ns ->
                            if (!idToName[ns].isNullOrBlank()) {
                                var currentPosition = component.position
                                var current = component.name
                                var prev = component.prev
                                var prevType = component.prevType
                                var compXP = 0
                                while (currentPosition > 1 && prev.isNotEmpty() && prevType.isNotEmpty() && gameParamsHM.containsKey(current)) {
                                    val comp: HashMap<String, Any> = mapper.convertValue(gameParamsHM[current], object: TypeReference<HashMap<String, Any>>() {})
                                    compXP += comp["costXP"] as Int
                                    if (prev.isNotEmpty()) {
                                        val tempSUList: List<ShipUpgrade> = ship.shipUpgradeInfo.components[prevType] ?: listOf()
                                        for (su in tempSUList) {
                                            if (su.name.equals(prev, ignoreCase = true)) {
                                                currentPosition = su.position
                                                prevType = su.prevType
                                                current = su.name
                                                prev = su.prev
                                                break
                                            }
                                        }
                                    } else {
                                        break
                                    }
                                }

                                ships[idToName[ns]]?.also {
                                    it.prevShipIndex = ship.index
                                    it.prevShipName = ship.name
                                    it.prevShipXP = ship.shipUpgradeInfo.costXP
                                    it.prevShipCompXP = compXP
                                }
                            }
                        }
                    }
                }
            }
        }

        ships.forEach { (_, ship) ->
            val fullName = global[EN]?.get("$IDS_${ship.index.toUpperCase()}_FULL").toString()

            if (fullName.isNotBlank() && !fullName.contains("ARP") && !ship.typeinfo.nation.isNullOrBlank() && !ship.typeinfo.species.isNullOrBlank()) {
                shipsList.putIfAbsent(ship.typeinfo.nation!!, LinkedHashMap())
                shipsList[ship.typeinfo.nation!!]?.putIfAbsent(ship.realShipTypeId.toUpperCase(), LinkedHashMap())
                shipsList[ship.typeinfo.nation!!]!![ship.realShipTypeId.toUpperCase()]!!.putIfAbsent(ship.typeinfo.species!!.toUpperCase(), LinkedHashMap())
                shipsList[ship.typeinfo.nation!!]!![ship.realShipTypeId.toUpperCase()]!![ship.typeinfo.species!!.toUpperCase()]!!.putIfAbsent(ship.level, mutableListOf())

                shipsList[ship.typeinfo.nation!!]!![ship.realShipTypeId.toUpperCase()]!![ship.typeinfo.species!!.toUpperCase()]!![ship.level]
                    ?.add(ShipIndex(ship, ship.shipUpgradeInfo.components[artillery]?.map { it.name } ?: listOf()))
            }
        }

        shipsList.entries.sortedBy { it.key }.forEach { nation ->
            nation.value.entries.sortedBy { it.key }.forEach { realShipType ->
                realShipType.value.forEach { (shipType, tiers) ->
                    tiers.entries.sortedBy { it.key }.forEach { tier ->
                        tier.value.sortBy { it.index }
                        shipsList[nation.key]?.get(realShipType.key)?.get(shipType)?.remove(tier.key)
                        shipsList[nation.key]?.get(realShipType.key)?.get(shipType)?.set(tier.key, tier.value)
                    }
                    var tier = 10
                    while (tier > 0) {
                        if (!tiers[tier].isNullOrEmpty()) {
                            tiers[tier]?.sortBy { it.index }
                            val cTier = tier
                            var pos = 1
                            tiers[tier]?.forEach { ship ->
                                if (ship.research) {
                                    if (ship.position == 0) {
                                        ship.position = pos
                                        pos++
                                    }
                                    if (!shipsList[nation.key]!![realShipType.key]!![shipType]!![cTier - 1].isNullOrEmpty()) {
                                        shipsList[nation.key]!![realShipType.key]!![shipType]!![cTier - 1]?.forEach { tShip ->
                                            if (ships[tShip.index]!!.typeinfo.species.equals(shipType, ignoreCase = true)) {
                                                ships[tShip.index]?.shipUpgradeInfo?.components?.forEach { (_, list) ->
                                                    list.forEach { u1 ->
                                                        if (u1.nextShips.contains(ship.identifier)) {
                                                            if (list.any { u2 -> u2.nextShips.isNotEmpty() }) {
                                                                tShip.position = ship.position
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            tiers[tier]?.sortBy { it.position }
                        }
                        tier--
                    }
                }
                if (realShipType.key.equals("FILTER_PREMIUM", ignoreCase = true)) {
                    shipsList[nation.key]?.remove(realShipType.key)
                    shipsList[nation.key]!![realShipType.key] = realShipType.value
                } else if (realShipType.key.equals("TEST_SAMPLE", ignoreCase = true)) {
                    shipsList[nation.key]?.remove(realShipType.key)
                    shipsList[nation.key]!![realShipType.key] = realShipType.value
                }
            }
            shipsList.remove(nation.key)
            shipsList[nation.key] = nation.value
        }
        val russia = shipsList["Russia"]
        shipsList.remove("Russia")
        shipsList["Russia"] = russia!!
    }

    private fun sortUpgrades()
    {
        upgrades.forEach { (_, mod) ->
            mod.entries.sortedWith(compareBy({ it.value.modifiers.costCR }, { it.key })).forEach { u ->
                mod.remove(u.key)
                mod[u.key] = u.value
            }
        }
    }

    private fun sortFlags()
    {
        flags.values.sortedBy { it.sortOrder }.forEach { flag ->
            flags.remove(flag.name)
            flags[flag.name] = flag
        }
    }

    private fun setCommanderParams()
    {
        commanders.forEach { (_, commander) ->
            commander.crewSkills.forEach { r ->
                val temp = mapper.readValue(mapper.writeValueAsString(r.value.modifiers), jacksonTypeRef<LinkedHashMap<String, Any?>>())
                val logicTrigger = mapper.convertValue(r.value.logicTrigger, jacksonTypeRef<LinkedHashMap<String, Any>>())
                val modifiers = mapper.convertValue(logicTrigger["modifiers"], jacksonTypeRef<LinkedHashMap<String, Any>>())

                temp.forEach { t ->
                    CommonUtils.getBonus(mapper.convertValue(t, object: TypeReference<LinkedHashMap<String, Any>>() {})).run {
                        this.forEach { (x, y) ->
                            r.value.bonus[x] = y
                        }
                    }
                }

                if (!modifiers.isNullOrEmpty()) {
                    CommonUtils.getBonus(modifiers).forEach { (t, u) ->
                        r.value.bonus[t] = u
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun generateShipData()
    {
        log.info("Generating ship data")

        val directory = getGameParamsDir().replace(FILE_GAMEPARAMS, DIR_SHIPS)
        var folder = getEmptyFolder(directory)

        for ((key, value) in ships) {
            val tempJson = "$directory$key$FILE_JSON"
            val f = File(tempJson)
            mapper.writeValue(f, value)
        }

        for ((key, value) in misc) {
            val tempJson = "$directory$key$FILE_JSON"
            val f = File(tempJson)
            mapper.writeValue(f, value)
        }

        var f = File("$directory$TYPE_SHIP_LIST$FILE_JSON")
        mapper.writeValue(f, shipsList)

        f = File("$directory$TYPE_UPGRADE$FILE_JSON")
        mapper.writeValue(f, upgrades)

        f = File("$directory$TYPE_CONSUMABLE$FILE_JSON")
        mapper.writeValue(f, consumables)

        f = File("$directory$TYPE_COMMANDER$FILE_JSON")
        mapper.writeValue(f, commanders)

        f = File("$directory$TYPE_FLAG$FILE_JSON")
        mapper.writeValue(f, flags)

        createZipFile(folder, directory.replace(DIR_SHIPS, FILE_SHIPS_ZIP))

        folder = getEmptyFolder(directory)
        folder.delete()

        log.info("Generated ship data")
    }

    @Throws(IOException::class)
    fun generateShellPenetration()
    {
        log.info("Generating shell penetration")

        val directory = getGameParamsDir().replace(FILE_GAMEPARAMS, DIR_SHELL)
        val folder = getEmptyFolder(directory)
        val shellsPen = mutableListOf<Shell>()

        val scope = CoroutineScope(executor.asCoroutineDispatcher() + job)
        runBlocking {
            val deferred = ships.map { (_, ship) ->
                scope.async {
                    if (ship.shipUpgradeInfo.components[artillery]!!.size > 0) {
                        ship.shipUpgradeInfo.components[artillery]!!.forEach { su ->
                            val tempId = su.components[artillery]!![su.components[artillery]!!.size - 1]

                            ship.components.artillery[tempId]!!.turrets[0].ammoList.forEach { ammo ->
                                val shell = shells[ammo]
                                if (shell != null) {
                                    penetrationUtils.setPenetration(
                                        shell,
                                        ship.components.artillery[tempId]!!.turrets[0].vertSector[1],
                                        ship.components.artillery[tempId]!!.minDistV,
                                        ship.components.artillery[tempId]!!.maxDist,
                                        AP.equals(shell.ammoType.toLowerCase(), ignoreCase = true)
                                    )
                                    shellsPen.add(shell)
                                }
                            }
                        }
                    }
                }
            }
            deferred.awaitAll()
        }

        shellsPen.forEach { shell ->
            val tempJson = "$directory${shell.name}$FILE_JSON"
            val f = File(tempJson)
            mapper.writeValue(f, shell)
        }
        createZipFile(folder, directory.replace(DIR_SHELL, FILE_SHELLS_ZIP))

        folder.listFiles { f -> f.delete() }
        folder.delete()

        log.info("Generated shell penetration")
    }

    @Throws(IOException::class)
    private fun getGameParamsDir(): String
    {
        return ClassPathResource("/json/live/GameParams.zip").url.path.replaceFirst(SLASH, "").also {
            if (it.startsWith("var") || it.startsWith("Users")) "${SLASH}${it}"
        }
    }

    private fun getEmptyFolder(directory: String): File
    {
        return File(directory).also {
            if (!it.exists() || !it.isDirectory) it.mkdir()
            else it.listFiles { f -> f.delete() }
        }
    }

    @Throws(IOException::class)
    private fun createZipFile(folder: File, directory: String)
    {
        val fos = FileOutputStream(directory)
        ZipOutputStream(fos).also {
            folder.listFiles()?.forEach { file ->
                it.putNextEntry(ZipEntry(file.name))
                it.write(file.readBytes())
                it.closeEntry()
            }
        }.close()
        fos.close()
    }
}
