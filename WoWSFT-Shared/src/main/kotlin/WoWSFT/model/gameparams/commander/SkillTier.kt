package WoWSFT.model.gameparams.commander

import com.fasterxml.jackson.annotation.JsonProperty

class SkillTier {
    @JsonProperty("AirCarrier")
    var aircraftCarrier: Int = 1

    @JsonProperty("Auxiliary")
    var auxiliary: Int = 1

    @JsonProperty("Battleship")
    var battleship: Int = 1

    @JsonProperty("Cruiser")
    var cruiser: Int = 1

    @JsonProperty("Destroyer")
    var destroyer: Int = 1

    @JsonProperty("Submarine")
    var submarine: Int = 1
}