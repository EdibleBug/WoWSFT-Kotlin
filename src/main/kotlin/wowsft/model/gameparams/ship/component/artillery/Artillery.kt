package wowsft.model.gameparams.ship.component.artillery;

import wowsft.config.WoWSFT;
import wowsft.model.gameparams.ship.component.airdefense.Aura;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@WoWSFT
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artillery
{
    private List<Aura> auraFar = new ArrayList<>();
    private List<Aura> auraMedium = new ArrayList<>();
    private List<Aura> auraNear = new ArrayList<>();

    private List<Turret> turrets = new ArrayList<>();
    private LinkedHashMap<Integer, List<Object>> turretTypes = new LinkedHashMap<>();

    private float artificialOffset;
    private float maxDist;
    private float minDistH;
    private float minDistV;
    private boolean normalDistribution;
    private float sigmaCount;
    private float taperDist;
    @JsonInclude
    private float GMIdealRadius = 1f;
    private float barrelDiameter;

    private LinkedHashMap<String, Shell> shells = new LinkedHashMap<>();

    @JsonIgnore
    private ObjectMapper mapper = new ObjectMapper();

    @JsonAnySetter
    public void setGuns(String name, Object value)
    {
        if (value instanceof HashMap) {
            HashMap<String, Object> tempObject = mapper.convertValue(value, new TypeReference<HashMap<String, Object>>(){});

            if ("far".equalsIgnoreCase((String) tempObject.get("type"))) {
                auraFar.add(mapper.convertValue(value, Aura.class));
            } else if ("medium".equalsIgnoreCase((String) tempObject.get("type"))) {
                auraMedium.add(mapper.convertValue(value, Aura.class));
            } else if ("near".equalsIgnoreCase((String) tempObject.get("type"))) {
                auraNear.add(mapper.convertValue(value, Aura.class));
            } else if (tempObject.containsKey("HitLocationArtillery")) {
                Turret turret = mapper.convertValue(value, Turret.class);
                turrets.add(turret);
                this.barrelDiameter = turret.getBarrelDiameter();

                if (turretTypes.containsKey(turret.getNumBarrels())) {
                    turretTypes.get(turret.getNumBarrels()).set(0, (int) turretTypes.get(turret.getNumBarrels()).get(0) + 1);
                } else {
                    List<Object> tObject = new ArrayList<>();
                    tObject.add(1);
                    tObject.add(turret.getName());
                    turretTypes.put(turret.getNumBarrels(), tObject);
                }
            }
        }
    }
}
