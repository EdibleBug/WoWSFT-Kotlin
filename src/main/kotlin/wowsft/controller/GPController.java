package wowsft.controller;

import wowsft.model.gameparams.commander.Commander;
import wowsft.model.gameparams.consumable.Consumable;
import wowsft.model.gameparams.flag.Flag;
import wowsft.model.gameparams.modernization.Modernization;
import wowsft.model.gameparams.ship.Ship;
import wowsft.model.gameparams.ship.ShipIndex;
import wowsft.model.gameparams.ship.component.artillery.Shell;
import wowsft.service.GPService;
import wowsft.service.ParamService;
import wowsft.service.ParserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static wowsft.model.Constant.*;

/**
 * Created by Aesis on 2016-10-15.
 */
@Slf4j
@Controller
public class GPController extends ExceptionController
{
    @Autowired
    @Qualifier(value = "loadFinish")
    private HashMap<String, Integer> loadFinish;

    @Autowired
    @Qualifier(value = "gameParamsHM")
    private HashMap<String, Object> gameParamsHM;

    @Autowired
    @Qualifier(value = "notification")
    private LinkedHashMap<String, LinkedHashMap<String, String>> notification;

    @Autowired
    @Qualifier(value = "translation")
    private LinkedHashMap<String, LinkedHashMap<String, String>> translation;

    @Autowired
    @Qualifier(value = "global")
    private HashMap<String, HashMap<String, Object>> global;

    @Autowired
    @Qualifier(value = TYPE_SHIP)
    private LinkedHashMap<String, Ship> ships;

    @Autowired
    @Qualifier(value =  TYPE_CONSUMABLE)
    private LinkedHashMap<String, Consumable> consumables;

    @Autowired
    @Qualifier(value = TYPE_SHIP_LIST)
    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<Integer, List<ShipIndex>>>>> shipsList;

    @Autowired
    @Qualifier(value = TYPE_UPGRADE)
    private LinkedHashMap<Integer, LinkedHashMap<String, Modernization>> upgrades;

    @Autowired
    @Qualifier(value = TYPE_COMMANDER)
    private LinkedHashMap<String, Commander> commanders;

    @Autowired
    @Qualifier(value = TYPE_FLAG)
    private LinkedHashMap<String, Flag> flags;

    @Autowired
    private GPService gpService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private ParserService parserService;

    private ObjectMapper mapper = new ObjectMapper();

    @ModelAttribute(name = "language")
    public void setLanguage(Model model, HttpServletRequest request, HttpServletResponse response)
    {
        String[] lang = request.getParameterMap().get("lang");
        if (lang == null || lang.length == 0) {
            model.addAttribute("lang", "en");
        } else {
            String l = "en";
            for (String s : lang) {
                if (globalLanguage.contains(s.toLowerCase())) {
                    l = s;
                    break;
                }
            }
            model.addAttribute("lang", l);
        }
    }

    @ResponseBody
    @GetMapping(value = "/data")
    public Object tester(@RequestParam(required = false, defaultValue = "") String type,
                         @RequestParam(required = false, defaultValue = "") String index,
                         @RequestParam(required = false, defaultValue = "") String lang) throws Exception
    {
        if (type.equalsIgnoreCase(TYPE_SHIP)) {
            if (StringUtils.isNotEmpty(lang)) {
                return gpService.getShip(index);
            }
            return ships.get(index);
        } else if (type.equalsIgnoreCase(TYPE_UPGRADE)) {
            return upgrades;
        } else if (type.equalsIgnoreCase(TYPE_CONSUMABLE)) {
            return consumables;
        } else if (type.equalsIgnoreCase(TYPE_COMMANDER)) {
            return commanders;
        } else if (type.equalsIgnoreCase(TYPE_SHIP_LIST)) {
            return shipsList;
        } else if (type.equalsIgnoreCase(TYPE_FLAG)) {
            return flags;
        }
        return gameParamsHM.get(index);
    }

    @GetMapping(value = "")
    public String getHome(Model model, @RequestParam(required = false, defaultValue = "en") String lang)
    {
        if (loadFinish.get("loadFinish") == 0) {
            return "loadPage";
        }

        model.addAttribute("notification", notification.get(lang.toLowerCase()));
        model.addAttribute("trans", translation.get(lang.toLowerCase()));

        return "home";
    }

    @RequestMapping(value = "/ship", method = { RequestMethod.GET, RequestMethod.POST })
    public String getWarship(HttpServletRequest request, Model model,
                             @RequestParam(required = false, defaultValue = "en") String lang,
                             @RequestParam(required = false, defaultValue = "") String index,
                             @RequestParam(required = false, defaultValue = "") String modules,
                             @RequestParam(required = false, defaultValue = "") String upgrades,
                             @RequestParam(required = false, defaultValue = "") String consumables,
                             @RequestParam(required = false, defaultValue = "PCW001") String commander,
                             @RequestParam(required = false, defaultValue = "0") long skills,
                             @RequestParam(required = false, defaultValue = "100") int ar) throws Exception
    {
        model.addAttribute("single", true);
        model.addAttribute("IDS", IDS);

        lang = globalLanguage.contains(lang) ? lang : "en";
        if (!"en".equalsIgnoreCase(lang.toLowerCase())) {
            model.addAttribute("english", global.get("en"));
        }
        model.addAttribute("global", global.get(lang.toLowerCase()));
        model.addAttribute("trans", translation.get(lang.toLowerCase()));

        if (StringUtils.isNotEmpty(index)) {
            model.addAttribute("index", index.toUpperCase());
            model.addAttribute("dataIndex", 0);
            model.addAttribute("commanders", commanders);
            skills = skills > maxBitsToInt ? 0 : skills;

            Ship ship = getShip(index.toUpperCase(), modules, upgrades, consumables, skills, commander.toUpperCase(), ar, false);
            model.addAttribute(TYPE_WARSHIP, ship);

//            log.info(request.getRequestURL() + (StringUtils.isNotEmpty(request.getQueryString()) ? "?" + request.getQueryString() : ""));

            if ("post".equalsIgnoreCase(request.getMethod())) {
                return "Joint/rightInfo :: rightInfo";
//                return "Joint/shipSelect :: warshipStats";
            }
        }
        model.addAttribute("nations", shipsList);

        return "FittingTool/ftHome";
    }

    @ResponseBody
    @PostMapping(value = "/shipData")
    public Ship getWarshipData(@RequestParam(required = false, defaultValue = "en") String lang,
                               @RequestParam(required = false, defaultValue = "") String index,
                               @RequestParam(required = false, defaultValue = "") String modules,
                               @RequestParam(required = false, defaultValue = "") String upgrades,
                               @RequestParam(required = false, defaultValue = "") String consumables,
                               @RequestParam(required = false, defaultValue = "PCW001") String commander,
                               @RequestParam(required = false, defaultValue = "0") long skills) throws Exception
    {
        if (StringUtils.isNotEmpty(index)) {
            return getShip(index.toUpperCase(), modules, upgrades, consumables, skills, commander.toUpperCase(), 100, true);
        }
        throw new NullPointerException();
    }

    @GetMapping(value = "/WarshipStats")
    public String legacyUrl(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String url = parserService.parseLegacyUrl(request);

        return "redirect:" + url;
    }

    private Ship getShip(String index, String modules, String upgrades, String consumables, long skills, String commander, int ar, boolean data) throws Exception
    {
        Ship ship = mapper.readValue(mapper.writeValueAsString(gpService.getShip(index)), Ship.class);
        parserService.parseModules(ship, modules);
        gpService.setShipAmmo(ship);
        parserService.parseConsumables(ship, consumables);
        parserService.parseUpgrades(ship, upgrades);
        parserService.parseSkills(ship, skills, ar);
        paramService.setAA(ship);

        if (!"PCW001".equals(commander)
                && (commanders.get(commander) == null || !commanders.get(commander).getCrewPersonality().getShips().getNation().contains(ship.getTypeinfo().getNation()))) {
            commander = "PCW001";
        }
        ship.setCommander(commanders.get(commander));
        paramService.setParameters(ship);

        return ship;
    }

    @RequestMapping(value = "/arty", method = RequestMethod.GET)
    public String getArtyChart(Model model,
                               @RequestParam(required = false, defaultValue = "en") String lang)
    {
        model.addAttribute("IDS", IDS);
        model.addAttribute("global", global.get(lang.toLowerCase()));
        if (!"en".equalsIgnoreCase(lang.toLowerCase())) {
            model.addAttribute("english", global.get("en"));
        }
        model.addAttribute("trans", translation.get(lang.toLowerCase()));
        model.addAttribute("nations", shipsList);

        return "ArtyChart/acHome";
    }

    @ResponseBody
    @RequestMapping(value = "/arty", method = RequestMethod.POST)
    public Shell getShellData(@RequestParam String index, @RequestParam String artyId) throws Exception
    {
        return gpService.getArtyAmmoOnly(index, artyId);
    }

    @GetMapping(value = "/research")
    public String getResearch(Model model,
                              @RequestParam(required = false, defaultValue = "en") String lang)
    {
        model.addAttribute("global", global.get(lang.toLowerCase()));
        if (!"en".equalsIgnoreCase(lang.toLowerCase())) {
            model.addAttribute("english", global.get("en"));
        }
        model.addAttribute("trans", translation.get(lang.toLowerCase().toLowerCase()));
        model.addAttribute("IDS", IDS);
        model.addAttribute("nations", shipsList);

        return "Research/shipTree";
    }
}