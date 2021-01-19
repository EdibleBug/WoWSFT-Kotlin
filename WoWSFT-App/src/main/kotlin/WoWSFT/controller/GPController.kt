package WoWSFT.controller

import WoWSFT.config.CustomProperties
import WoWSFT.model.Constant.*
import WoWSFT.model.custom.CustomSkill
import WoWSFT.model.gameparams.commander.Commander
import WoWSFT.model.gameparams.flag.Flag
import WoWSFT.model.gameparams.ship.Ship
import WoWSFT.model.gameparams.ship.ShipIndex
import WoWSFT.model.gameparams.ship.component.artillery.Shell
import WoWSFT.service.GPService
import WoWSFT.service.ParamService
import WoWSFT.service.ParserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class GPController(
    private val customProperties: CustomProperties,
    @Qualifier(LOAD_FINISH) private val loadFinish: HashMap<String, Int>,
    @Qualifier(NOTIFICATION) private val notification: LinkedHashMap<String, LinkedHashMap<String, String>>,
    @Qualifier(GLOBAL) private val global: HashMap<String, HashMap<String, Any>>,
    @Qualifier(TYPE_SHIP_LIST) private val shipsList: LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<Int, List<ShipIndex>>>>>,
    @Qualifier(TYPE_COMMANDER) private val commanders: LinkedHashMap<String, Commander>,
    @Qualifier(TYPE_FLAG) private val flagsLHM: LinkedHashMap<String, Flag>,
    @Qualifier(CUSTOM_SKIlL_GROUP) private val skillGroup: HashMap<String, List<List<CustomSkill>>>,
    private val gpService: GPService,
    private val paramService: ParamService,
    private val parserService: ParserService
) : ExceptionController()
{
    companion object {
        private val mapper = ObjectMapper()
        private const val lang = EN
        private const val WOWSFT_AD = "WoWSFT_Ad"
    }
    private val isRelease get() = "release".equals(customProperties.env, ignoreCase = true)

    @ModelAttribute(name = "misc")
    fun setLanguage(model: Model, request: HttpServletRequest)
    {
        model.addAttribute("lang", lang)
        getAdStatus(request).let { model.addAttribute("adStatus", it == null || it.value == "1") }
    }

    @GetMapping("")
    fun getHome(model: Model): String
    {
        if (loadFinish[LOAD_FINISH] == 0) {
            return "loadPage"
        }
        model.addAttribute(NOTIFICATION, notification[lang])

        return "home"
    }

    @RequestMapping(value = ["/ship", "/compare"], method = [RequestMethod.GET, RequestMethod.POST])
    @Throws(Exception::class)
    fun getWarship(
        request: HttpServletRequest,
        model: Model,
        @RequestParam(required = false, defaultValue = "") index: String,
        @RequestParam(required = false, defaultValue = "") modules: String,
        @RequestParam(required = false, defaultValue = "") upgrades: String,
        @RequestParam(required = false, defaultValue = "") consumables: String,
        @RequestParam(required = false, defaultValue = "PCW001") commander: String,
        @RequestParam(required = false, defaultValue = "0") skills: Long,
        @RequestParam(required = false, defaultValue = "0") flags: Int,
        @RequestParam(required = false, defaultValue = "100") ar: Int,
        @RequestParam(required = false, defaultValue = "0") pos: Int
    ): String
    {
        if (request.requestURI.equals("/ship", ignoreCase = true) || request.method.equals("post", ignoreCase = true)) {
            model.addAttribute("single", pos == 0)
            model.addAttribute(IDS, IDS_)
            model.addAttribute(GLOBAL, global[lang])
        }

        if (index.isNotEmpty()) {
            model.addAttribute("index", index.toUpperCase())
            model.addAttribute("dataIndex", pos)
            model.addAttribute("commanders", commanders)
            model.addAttribute("flags", flagsLHM)
            model.addAttribute("skillGroup", skillGroup)

            val sSkills = if (skills > maxBitsToInt) 0 else skills
            val ship = getShip(index.toUpperCase(), modules, upgrades, consumables, sSkills, commander.toUpperCase(), flags, ar)
            model.addAttribute(TYPE_WARSHIP, ship)

            if ("post".equals(request.method, ignoreCase = true)) {
                return if (request.requestURI.equals("/ship", ignoreCase = true)) {
                    "Joint/rightInfo :: rightInfo"
                } else {
                    "Joint/shipSelect :: warshipStats"
                }
            }
        }

        return if (request.requestURI.equals("/ship", ignoreCase = true)) {
            model.addAttribute("nations", shipsList)

            "FittingTool/ftHome"
        } else {
            "Comparison/comparison"
        }
    }

    @Throws(Exception::class)
    private fun getShip(index: String, modules: String, upgrades: String, consumables: String, skills: Long, commander: String, flags: Int, ar: Int): Ship
    {
        var sCommander = commander
        val ship = mapper.readValue(mapper.writeValueAsString(gpService.getShip(index)), Ship::class.java)

        parserService.parseModules(ship, modules)
        gpService.setShipAmmo(ship)

        parserService.parseConsumables(ship, consumables)
        parserService.parseUpgrades(ship, upgrades)
        parserService.parseFlags(ship, flags)
        parserService.parseSkills(ship, skills, ar)
        paramService.setAA(ship)

        if ("PCW001" != sCommander && (commanders[sCommander] == null || !commanders[sCommander]!!.crewPersonality.ships.nation.contains(ship.typeinfo.nation))) {
            sCommander = "PCW001"
        }
        ship.commander = commanders[sCommander]

        paramService.setParameters(ship)

        return ship
    }

    @GetMapping("/arty")
    fun getArtyChart(model: Model): String
    {
        model.addAttribute(IDS, IDS_)
        model.addAttribute(GLOBAL, global[lang])
        model.addAttribute("nations", shipsList)

        return "ArtyChart/acHome"
    }

    @ResponseBody
    @PostMapping("/arty")
    @Throws(Exception::class)
    fun getShellData(@RequestParam index: String, @RequestParam artyId: String): Shell?
    {
        return gpService.getArtyAmmoOnly(index, artyId)
    }

    @GetMapping("/research")
    fun getResearch(model: Model): String
    {
        model.addAttribute(GLOBAL, global[lang])
        model.addAttribute(IDS, IDS_)
        model.addAttribute("nations", shipsList)

        return "Research/shipTree"
    }

    @ResponseBody
    @PostMapping("/adToggle")
    fun toggleAdRequest(request: HttpServletRequest,
                        response: HttpServletResponse, @RequestParam toggle: Boolean): String
    {
        toggleAd(response, toggle)

        return "SUCCESS"
    }

    private fun getAdStatus(request: HttpServletRequest): Cookie?
    {
        return if (!request.cookies.isNullOrEmpty()) request.cookies.firstOrNull { c -> c.name == WOWSFT_AD } else null
    }

    private fun toggleAd(response: HttpServletResponse, toggle: Boolean)
    {
        val toggleValue = if (toggle) "1" else "0"
        val domain = if (isRelease) "wowsft.com" else "localhost"
        val maxAge = 31556952L

        val resCookie = ResponseCookie
            .from(WOWSFT_AD, toggleValue).domain(domain).path("/").maxAge(maxAge)
            .httpOnly(true).sameSite("Strict").secure(isRelease)
            .build()

        response.setHeader(HttpHeaders.SET_COOKIE, resCookie.toString())
    }
}