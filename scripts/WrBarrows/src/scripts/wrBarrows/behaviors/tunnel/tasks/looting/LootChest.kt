package scripts.wrBarrows.behaviors.tunnel.tasks.looting

import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.WorldTile

class LootChest {
    fun satisfied(): Boolean {
        return !this.isInsideLootChamber()
    }

    fun execute() {
        // First "Open" the "Chest"

        // BEWARE: a Brother can spawn

        // Then, "Search" the "Chest" to actually loot

    }

    private fun isInsideLootChamber(): Boolean {
        val area = Area.fromRectangle(
            WorldTile(3547, 9699, 0),
            WorldTile(3556, 9690, 0)
        )

        return area.containsMyPlayer()
    }
}