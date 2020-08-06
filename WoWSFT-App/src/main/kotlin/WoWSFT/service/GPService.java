package WoWSFT.service;

import WoWSFT.model.gameparams.consumable.Consumable;
import WoWSFT.model.gameparams.modernization.Modernization;
import WoWSFT.model.gameparams.ship.Ship;
import WoWSFT.model.gameparams.ship.abilities.AbilitySlot;
import WoWSFT.model.gameparams.ship.component.artillery.Shell;
import WoWSFT.model.gameparams.ship.component.atba.Secondary;
import WoWSFT.model.gameparams.ship.component.planes.Plane;
import WoWSFT.model.gameparams.ship.component.torpedo.TorpedoAmmo;
import WoWSFT.model.gameparams.ship.upgrades.ShipUpgrade;
import WoWSFT.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import static WoWSFT.model.Constant.*;

@Service
@Slf4j
public class GPService
{
    private final LinkedHashMap<String, Consumable> consumables;
    private final LinkedHashMap<Integer, LinkedHashMap<String, Modernization>> upgrades;
    private final ZipFile zShip;
    private final ZipFile zShell;

    private ObjectMapper mapper = new ObjectMapper();

    public GPService(
            @Qualifier(value = TYPE_CONSUMABLE) LinkedHashMap<String, Consumable> consumables,
            @Qualifier(value = TYPE_UPGRADE) LinkedHashMap<Integer, LinkedHashMap<String, Modernization>> upgrades,
            @Qualifier(value = TYPE_SHIP) ZipFile zShip,
            @Qualifier(value = TYPE_SHELL) ZipFile zShell)
    {
        this.consumables = consumables;
        this.upgrades = upgrades;
        this.zShip = zShip;
        this.zShell = zShell;
    }

    //    @Cacheable(value = "ship", key = "#index")
    public Ship getShip(String index) throws Exception
    {
        Ship ship = (Ship) CommonUtils.zFetch(zShip, index, Ship.class);

        if (ship != null) {
            setUpgrades(ship);
            setConsumables(ship);

            return ship;
        }
        throw new NullPointerException();
    }

    private void setUpgrades(Ship ship)
    {
        List<List<Modernization>> upgradesList = new ArrayList<>();

        upgrades.forEach((slot, upgrades) -> upgrades.forEach((key, upgrade) -> {
            if ((!upgrade.getExcludes().contains(ship.getName()) && upgrade.getGroup().contains(ship.getGroup()) && upgrade.getNation().contains(ship.getTypeinfo().getNation())
                && upgrade.getShiptype().contains(ship.getTypeinfo().getSpecies()) && upgrade.getShiplevel().contains(ship.getLevel())) || upgrade.getShips().contains(ship.getName())) {
                if (upgradesList.size() < upgrade.getSlot() + 1) {
                    upgradesList.add(new ArrayList<>());
                }
                upgradesList.get(upgrade.getSlot()).add(upgrade);
            }
        }));

        int maxRows = upgradesList.stream().mapToInt(List::size).filter(slot -> slot > 0).max().orElse(0);
        ship.setUpgradesRow(maxRows);
        ship.setUpgrades(upgradesList);
    }

    private void setConsumables(Ship ship) throws Exception
    {
        List<List<Consumable>> consumablesList = new ArrayList<>();
        for (Map.Entry<String, AbilitySlot> entry : ship.getShipAbilities().entrySet()) {
            for (List<String> consumable : entry.getValue().getAbils()) {
                if (!consumable.get(0).contains("Super")) {
                    Consumable tempConsumable = mapper.readValue(mapper.writeValueAsString(consumables.get(consumable.get(0))), Consumable.class);
                    tempConsumable.getSubConsumables().entrySet().removeIf(e -> !e.getKey().equalsIgnoreCase(consumable.get(1)));
                    if (consumablesList.size() < entry.getValue().getSlot() + 1) {
                        consumablesList.add(new ArrayList<>());
                    }
                    consumablesList.get(entry.getValue().getSlot()).add(tempConsumable);
                }
            }
        }
        ship.setConsumables(consumablesList);
    }

    public void setShipAmmo(Ship ship) throws Exception
    {
        if (ship.getComponents().getArtillery().size() > 0 && ship.getComponents().getArtillery().get(ship.getModules().get(artillery)) != null) {
            for (String ammo : ship.getComponents().getArtillery().get(ship.getModules().get(artillery)).getTurrets().get(0).getAmmoList()) {
                Shell shell = (Shell) CommonUtils.zFetch(zShip, ammo, Shell.class);
//                PenetrationUtils.setPenetration(shell,
//                        ship.getComponents().getArtillery().get(ship.getModules().get(artillery)).getTurrets().get(0).getVertSector().get(1),
//                        ship.getComponents().getArtillery().get(ship.getModules().get(artillery)).getMinDistV(),
//                        ship.getComponents().getArtillery().get(ship.getModules().get(artillery)).getMaxDist(),
//                        AP.equalsIgnoreCase(shell.getAmmoType().toLowerCase()));

                ship.getComponents().getArtillery().get(ship.getModules().get(artillery)).getShells()
                        .put(ammo, shell);
            }
        }

        if (ship.getComponents().getTorpedoes().size() > 0 && ship.getComponents().getTorpedoes().get(ship.getModules().get(torpedoes)) != null) {
            String ammo = ship.getComponents().getTorpedoes().get(ship.getModules().get(torpedoes)).getLaunchers().get(0).getAmmoList().get(0);
            ship.getComponents().getTorpedoes().get(ship.getModules().get(torpedoes))
                    .setAmmo((TorpedoAmmo) CommonUtils.zFetch(zShip, ammo, TorpedoAmmo.class));
        }

        if (ship.getComponents().getAtba().size() > 0 && ship.getComponents().getAtba().get(ship.getModules().get(atba)) != null) {
            for (Map.Entry<String, Secondary> secondary : ship.getComponents().getAtba().get(ship.getModules().get(atba)).getSecondaries().entrySet()) {
                Shell ammo = (Shell) CommonUtils.zFetch(zShip, secondary.getValue().getAmmoList().get(0), Shell.class);
                if (ammo != null) {
                    secondary.getValue().setAlphaDamage(ammo.getAlphaDamage());
                    secondary.getValue().setAlphaPiercingHE(ammo.getAlphaPiercingHE());
                    secondary.getValue().setAmmoType(ammo.getAmmoType());
                    secondary.getValue().setBulletSpeed(ammo.getBulletSpeed());
                    secondary.getValue().setBurnProb(ammo.getBurnProb());
                }
            }
        }

        if (ship.getPlanes().size() > 0) {
            if (ship.getModules().get(diveBomber) != null && ship.getPlanes().get(ship.getModules().get(diveBomber)) != null) {
                Plane dbPlane = (Plane) CommonUtils.zFetch(zShip, ship.getPlanes().get(ship.getModules().get(diveBomber)), Plane.class);
                if (dbPlane != null) {
                    dbPlane.setBomb((Shell) CommonUtils.zFetch(zShip, dbPlane.getBombName(), Shell.class));
                    setPlaneConsumables(dbPlane);
                    ship.getComponents().getDiveBomber().put(ship.getModules().get(diveBomber), dbPlane);
                }
            }

            if (ship.getModules().get(fighter) != null && ship.getPlanes().get(ship.getModules().get(fighter)) != null) {
                Plane fPlane = (Plane) CommonUtils.zFetch(zShip, ship.getPlanes().get(ship.getModules().get(fighter)), Plane.class);
                if (fPlane != null) {
                    fPlane.setRocket((Shell) CommonUtils.zFetch(zShip, fPlane.getBombName(), Shell.class));
                    setPlaneConsumables(fPlane);
                    ship.getComponents().getFighter().put(ship.getModules().get(fighter), fPlane);
                }
            }

            if (ship.getModules().get(torpedoBomber) != null && ship.getPlanes().get(ship.getModules().get(torpedoBomber)) != null) {
                Plane tbPlane = (Plane) CommonUtils.zFetch(zShip, ship.getPlanes().get(ship.getModules().get(torpedoBomber)), Plane.class);
                if (tbPlane != null) {
                    tbPlane.setTorpedo((TorpedoAmmo) CommonUtils.zFetch(zShip, tbPlane.getBombName(), TorpedoAmmo.class));
                    setPlaneConsumables(tbPlane);
                    ship.getComponents().getTorpedoBomber().put(ship.getModules().get(torpedoBomber), tbPlane);
                }
            }
        }
    }

    private void setPlaneConsumables(Plane plane) throws Exception
    {
        List<Consumable> consumablesList = new ArrayList<>();

        for (Map.Entry<String, AbilitySlot> entry : plane.getPlaneAbilities().entrySet()) {
            for (List<String> consumable : entry.getValue().getAbils()) {
                if (!consumable.get(0).contains("Super")) {
                    Consumable tempConsumable = mapper.readValue(mapper.writeValueAsString(consumables.get(consumable.get(0))), Consumable.class);
                    tempConsumable.getSubConsumables().entrySet().removeIf(e -> !e.getKey().equalsIgnoreCase(consumable.get(1)));
                    consumablesList.add(tempConsumable);
                }
            }
        }
        plane.setConsumables(consumablesList);
    }

    public Shell getArtyAmmoOnly(String index, String artyId) throws Exception
    {
        Ship ship = (Ship) CommonUtils.zFetch(zShip, index, Ship.class);
        
        if (ship != null && ship.getShipUpgradeInfo().getComponents().get(artillery).size() > 0) {
            for (ShipUpgrade su : ship.getShipUpgradeInfo().getComponents().get(artillery)) {
                if (su.getName().equalsIgnoreCase(artyId)) {
                    String tempId = su.getComponents().get(artillery).get(su.getComponents().get(artillery).size() - 1);

                    for (String ammo : ship.getComponents().getArtillery().get(tempId).getTurrets().get(0).getAmmoList()) {
                        Shell shell = (Shell) CommonUtils.zFetch(zShell, ammo, Shell.class);
                        if (shell != null) {
                            return shell;
                        }

//                        Shell shell = mapper.readValue(mapper.writeValueAsString(zFetch(zShell, ammo)), Shell.class);
//                        if (AP.equalsIgnoreCase(shell.getAmmoType())) {
//                            PenetrationUtils.setPenetration(shell,
//                                    ship.getComponents().getArtillery().get(tempId).getTurrets().get(0).getVertSector().get(1),
//                                    ship.getComponents().getArtillery().get(tempId).getMinDistV(),
//                                    ship.getComponents().getArtillery().get(tempId).getMaxDist(),
//                                    AP.equalsIgnoreCase(shell.getAmmoType().toLowerCase()));
//
//                            return shell;
//                        }
                    }
                }
            }
        }
        return null;
    }
}
