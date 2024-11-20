package scripts.wrBlastFurnace.gui

import org.tribot.script.sdk.Camera
import scripts.wrBlastFurnace.behaviours.furnace.bars.BronzeBar
import scripts.wrBlastFurnace.behaviours.furnace.bars.IronBar
import scripts.wrBlastFurnace.behaviours.furnace.bars.MeltableBar
import scripts.wrBlastFurnace.behaviours.furnace.bars.SteelBar

object Settings {
    var barType: MeltableBar = SteelBar
    var world: String = "352"
    var staminaChecked: Boolean = true
    var coalBagChecked: Boolean = true

    var zoom: String = "mouse"
    var rotate: String = "mouse"
    var chatbox: String = "hidden"
    var preWalkChecked: Boolean = true

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

    fun toSerializable(): SerializableSettings {
        return SerializableSettings(
            barType = this.barType::class.java.simpleName,
            world = this.world,
            staminaChecked = this.staminaChecked,
            coalBagChecked = this.coalBagChecked,
            zoom = this.zoom,
            rotate = this.rotate,
            chatbox = this.chatbox,
            preWalkChecked = this.preWalkChecked,
            minAmount = this.minAmount,
            maxAmount = this.maxAmount,
            discordUrl = this.discordUrl,
            interval = this.interval
        )
    }

    fun fromSerializable(serializableSettings: SerializableSettings) {
        this.barType = when (serializableSettings.barType) {
            "SteelBar" -> SteelBar
            "IronBar" -> IronBar
            "BronzeBar" -> BronzeBar
            else -> throw IllegalArgumentException("Unknown bar type: ${serializableSettings.barType}")
        }
        this.world = serializableSettings.world
        this.staminaChecked = serializableSettings.staminaChecked
        this.coalBagChecked = serializableSettings.coalBagChecked
        this.zoom = serializableSettings.zoom
        this.rotate = serializableSettings.rotate
        this.chatbox = serializableSettings.chatbox
        this.preWalkChecked = serializableSettings.preWalkChecked
        this.minAmount = serializableSettings.minAmount
        this.maxAmount = serializableSettings.maxAmount
        this.discordUrl = serializableSettings.discordUrl
        this.interval = serializableSettings.interval
    }
}
