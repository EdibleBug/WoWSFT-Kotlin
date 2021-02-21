package WoWSFT.model.gameparams.flag

import WoWSFT.config.WoWSFT
import WoWSFT.model.Constant.CDN_IMAGE
import WoWSFT.model.gameparams.CommonModifier
import WoWSFT.model.gameparams.TypeInfo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@WoWSFT
@JsonIgnoreProperties(ignoreUnknown = true)
class Flag
{
    var canBuy = false
    var canBuyCustom: Any? = null
    var canCharge = false
    var canSell = false
    var costCR = 0
    var costGold = 0
    var flags = listOf<String>()
    var group = 0
    var hidden = false
    var hiddenCustom: Any? = null
    var id = 0L
    var index = ""
    var name = ""
    var typeinfo = TypeInfo()
    var modifiers = CommonModifier()
    var sortOrder = 0
    var identifier = ""
    var bonus = LinkedHashMap<String, String>()
    var description = ""
    val image get() = "$CDN_IMAGE/signal_flags/$name.png"
}