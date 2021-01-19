package WoWSFT.model.gameparams.commander

import WoWSFT.config.WoWSFT
import WoWSFT.model.gameparams.TypeInfo
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

@WoWSFT
@JsonIgnoreProperties(ignoreUnknown = true)
class Commander
{
    @JsonProperty("CrewPersonality")
    var crewPersonality = CrewPersonality()
    var id = 0L
    var identifier = ""
    var index = ""
    var name = ""
    var typeinfo = TypeInfo()
    @JsonProperty("CrewSkills")
    var crewSkills = LinkedHashMap<String, Skill>()

    companion object {
        @JsonIgnore
        private val mapper = ObjectMapper()
    }

    @JsonAnySetter
    fun setSkills(name: String, value: Any?) {
        if (name.equals("Skills", ignoreCase = true)) {
            val temp = mapper.convertValue(value, object : TypeReference<LinkedHashMap<String, Skill>>() {})
            temp.forEach { (k: String, v: Skill) ->
                v.name = k
                v.nameSplit = "(?<=[a-zA-Z])[A-Z]".toRegex().replace(k) { "_${it.value}" }.toLowerCase()
                crewSkills[k] = v
            }
        }
    }
}