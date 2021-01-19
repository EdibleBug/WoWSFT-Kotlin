package WoWSFT.model.custom

import WoWSFT.model.Constant.CDN_IMAGE

class CustomSkill {
    var name: String = ""
        set(value) {
            field = value
            nameSplit = "(?<=[a-zA-Z])[A-Z]".toRegex().replace(name) { "_${it.value}" }.toLowerCase()
            image = "$CDN_IMAGE/skills/$nameSplit.png"
        }
    var nameSplit: String = ""
    var image: String = ""
    var tier: Int = 0
    var column: Int = 0
}