package WoWSFT.model.gameparams.commander

import WoWSFT.config.WoWSFT
import WoWSFT.model.Constant.CDN_IMAGE
import WoWSFT.model.gameparams.CommonModifier
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

@WoWSFT
class Skill : CommonModifier()
{
    var tier = SkillTier()

    var skillType = 0

    var canBeLearned = true

    @JsonAlias("isEpic")
    var epic = false

    var bonus = LinkedHashMap<String, String>()

    var description = ""

    @JsonProperty("LogicTrigger")
    var logicTrigger = LogicTrigger()

    var name = ""

    var nameSplit = ""

    val image get() = "$CDN_IMAGE/skills/$nameSplit.png"


}