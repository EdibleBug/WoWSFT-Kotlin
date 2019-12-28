package WoWSFT.model.gameparams.ship.component.airdefense

import WoWSFT.config.WoWSFT
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

@WoWSFT
@JsonIgnoreProperties(ignoreUnknown = true)
class AirDefense
{
    val auraFar = mutableListOf<Aura>()
    val auraMedium = mutableListOf<Aura>()
    val auraNear = mutableListOf<Aura>()
    val ownerlessTracesScatterCoefficient = 0.0
    val prioritySectorChangeDelay = 0.0
    val prioritySectorDisableDelay = 0.0
    val prioritySectorEnableDelay = 0.0
    val prioritySectorStrength = 0.0
    val sectors = mutableListOf<MutableList<Double>>()
    var prioritySectorPhases = mutableListOf<MutableList<Any>>()
        set(value) {
            field = value
            if (value.size == 2) {
                prioritySectorPreparation = value[0][0].toString().toDouble()
                prioritySectorDuration = value[1][0].toString().toDouble()
                prioritySectorDamageInitial = value[0][2].toString().toDouble()
                prioritySectorCoefficientInitial = value[0][3].toString().toDouble()
                prioritySectorCoefficientDuring = value[0][4].toString().toDouble()
            }
        }

    var prioritySectorPreparation = 0.0
    var prioritySectorDuration = 0.0
    var prioritySectorDamageInitial = 0.0
    var prioritySectorCoefficientInitial = 0.0
    var prioritySectorCoefficientDuring = 0.0

    @JsonIgnore
    private val mapper = ObjectMapper()

    @JsonAnySetter
    fun setAura(name: String, value: Any?) {
        if (value is HashMap<*, *>) {
            val tempObject = mapper.convertValue(value, object : TypeReference<HashMap<String, Any>>() {})
            when (tempObject["type"].toString().toLowerCase()) {
                "far" -> { auraFar.add(mapper.convertValue(value, Aura::class.java)) }
                "medium" -> { auraMedium.add(mapper.convertValue(value, Aura::class.java)) }
                "near" -> { auraNear.add(mapper.convertValue(value, Aura::class.java)) }
            }
        }
    }
}