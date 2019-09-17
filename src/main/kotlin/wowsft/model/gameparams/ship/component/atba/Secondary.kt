package wowsft.model.gameparams.ship.component.atba;

import wowsft.config.WoWSFT;
import wowsft.model.gameparams.TypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@WoWSFT
@JsonIgnoreProperties(ignoreUnknown = true)
public class Secondary
{
    private List<String> ammoList;
    private float antiAirAuraDistance;
    private float antiAirAuraStrength;
    private float barrelDiameter;
    private List<List<Float>> deadZone;
    private float delim;
    private List<Float> horizSector;
    private long id;
    private float idealDistance;
    private float idealRadius;
    private String index;
    private float minRadius;
    private String name;
    private int numBarrels;
    private float radiusOnDelim;
    private float radiusOnMax;
    private float radiusOnZero;
    private List<Float> rotationSpeed;
    private float shotDelay;
    private boolean smallGun;
    private float smokePenalty;
    private TypeInfo typeinfo;
    private List<Float> vertSector;
    @JsonInclude
    private float GSIdealRadius = 1f;

    private int count;

    private float alphaDamage;
    private float alphaPiercingHE;
    private String ammoType;
    private float bulletSpeed;
    private float burnProb;

    public float getAlphaPiercingHEReal()
    {
        return alphaPiercingHE - 1f;
    }
}
