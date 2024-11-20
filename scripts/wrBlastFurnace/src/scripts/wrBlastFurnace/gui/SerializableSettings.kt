package scripts.wrBlastFurnace.gui

data class SerializableSettings(
    val barType: String,
    val world: String,
    val staminaChecked: Boolean,
    val coalBagChecked: Boolean,
    val zoom: String,
    val rotate: String,
    val chatbox: String,
    val preWalkChecked: Boolean,
    val minAmount: String,
    val maxAmount: String,
    val discordUrl: String,
    val interval: String
)
