package scripts.wrCrafting


import org.tribot.script.sdk.types.Area
import scripts.utils.Logger

interface LogisticsManagerInterface {
    val logger: Logger
    val skillingArea: Area
}

fun LogisticsManagerInterface.inSkillingArea() = skillingArea.containsMyPlayer()

class LogisticsManager(
    override val logger: Logger,
    override val skillingArea: Area
) : LogisticsManagerInterface {

}