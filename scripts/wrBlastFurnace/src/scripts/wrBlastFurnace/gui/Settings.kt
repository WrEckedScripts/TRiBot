package scripts.wrBlastFurnace.gui

import org.tribot.script.sdk.Camera
import scripts.wrBlastFurnace.behaviours.furnace.bars.MeltableBar
import scripts.wrBlastFurnace.behaviours.furnace.bars.SteelBar

object Settings {
    var barType: MeltableBar = SteelBar()
    var world: String = "352"
    var staminaChecked: Boolean = true

    var zoom: String = "mouse"
    var rotate: String = "mouse"
    var chatbox: String = "hidden"
    var preWalkChecked: Boolean = false

    var minAmount: String = "150000"
    var maxAmount: String = "3750000"

    var discordUrl: String = ""
    var interval: String = "60"

    fun getZoomMethod(): Camera.ZoomMethod {
        if (this.zoom == "mouse") {
            return Camera.ZoomMethod.MOUSE_SCROLL
        }

        return Camera.ZoomMethod.OPTIONS_TAB
    }

    fun getRotateMethod(): Camera.RotationMethod {
        if (this.rotate == "mouse") {
            return Camera.RotationMethod.MOUSE
        }

        return Camera.RotationMethod.KEYS
    }

    fun getHideChatbox(): Boolean {
        return this.chatbox == "hidden"
    }

    fun getWorld(): Int {
        return this.world.toInt()
    }

    fun usesDiscord(): Boolean {
        return !(this.discordUrl == "" || this.interval == "")
    }
}
