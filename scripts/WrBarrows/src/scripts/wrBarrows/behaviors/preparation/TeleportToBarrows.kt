package scripts.wrBarrows.behaviors.preparation

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.InventoryItem
import scripts.wrBarrows.player.BarrowsArea
import kotlin.jvm.optionals.getOrNull

class TeleportToBarrows {
    fun satisfied(): Boolean {
        return BarrowsArea.BARROWS.surface.containsMyPlayer()
    }

    fun execute(): Boolean {
        if (null == this.getTeleportTablet()) {
            //out of teletabs
            return false
        }

        return this.getTeleportTablet()?.click("Break") ?: false
    }

    private fun getTeleportTablet(): InventoryItem? {
        return Query.inventory()
            .idEquals(19629)
            .findFirst()
            .getOrNull()
    }
}