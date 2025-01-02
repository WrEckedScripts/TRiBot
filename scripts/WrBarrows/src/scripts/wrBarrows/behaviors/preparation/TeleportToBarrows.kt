package scripts.wrBarrows.behaviors.preparation

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.InventoryItem
import scripts.utils.Logger
import scripts.wrBarrows.player.BarrowsArea
import kotlin.jvm.optionals.getOrNull

class TeleportToBarrows {
    fun satisfied(): Boolean {
        return BarrowsArea.BARROWS.surface.containsMyPlayer()
    }

    fun execute(): Boolean {
        if (this.satisfied()) {
            Logger("TeleportToBarrows").debug("execute() - We're already at barrows")
            return false
        }

        if (null == this.getTeleportTablet()) {
            //out of teletabs
            Logger("TeleportToBarrows").debug("execute() - Out of teleport tabs")
            return false
        }

        Logger("TeleportToBarrows").debug("execute() - Going to teleport, did we?")
        return this.getTeleportTablet()?.click("Break") ?: false
    }

    private fun getTeleportTablet(): InventoryItem? {
        return Query.inventory()
            .idEquals(19629)
            .findFirst()
            .getOrNull()
    }
}