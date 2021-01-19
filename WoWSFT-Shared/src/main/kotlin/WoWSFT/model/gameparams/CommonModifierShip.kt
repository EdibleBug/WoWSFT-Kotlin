package WoWSFT.model.gameparams

import com.fasterxml.jackson.annotation.JsonProperty

class CommonModifierShip {
    constructor()
    constructor(value: Double) {
        aircraftCarrier = value
        auxiliary = value
        battleship = value
        cruiser = value
        destroyer = value
        submarine = value
    }

    @JsonProperty("AirCarrier")
    var aircraftCarrier = 1.0

    @JsonProperty("Auxiliary")
    var auxiliary = 1.0

    @JsonProperty("Battleship")
    var battleship = 1.0

    @JsonProperty("Cruiser")
    var cruiser = 1.0

    @JsonProperty("Destroyer")
    var destroyer = 1.0

    @JsonProperty("Submarine")
    var submarine = 1.0
}