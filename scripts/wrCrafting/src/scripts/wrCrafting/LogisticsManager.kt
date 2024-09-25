package scripts.wrCrafting


import org.tribot.script.sdk.types.Area
import scripts.utils.Logger

interface LogisticsManagerInterface {
    val skillingArea: Area

    fun logger(): Logger
}

fun LogisticsManagerInterface.inSkillingArea(): Boolean {
    return skillingArea.containsMyPlayer()
}

class LogisticsManager(
    override val skillingArea: Area
) : LogisticsManagerInterface {
    override fun logger(): Logger {
        return Logger("Logistics")
    }
}