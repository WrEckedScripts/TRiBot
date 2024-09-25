package scripts.wrCombat

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.GroundItemQuery
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.GroundItem

data class LootItem(
    val itemId: Int,
    val itemName: String,
    val stack: Int,
)

class LootingManager(private val lootArea: Area) {
    private val logger = Logger("Looting Manager")

    val shouldLoot
        get() =
            isAnyLoot()

    private val lootQuery: GroundItemQuery
        get() =
            Query.groundItems()
                .inArea(lootArea)
                .isReachable

    fun manageLooting(): List<LootItem> {
        if (!shouldLoot) return emptyList()
        return loot()
    }

    private fun isAnyLoot() = lootQuery.isAny

    private fun getAllLoot() = lootQuery.toList()

    private fun loot(): List<LootItem> {
        val lootItems = getAllLoot()
        if (lootItems.isEmpty() || Inventory.isFull()) return emptyList()

        logger.debug("Looting: ${lootItems.joinToString(", ") { it.name }}")

        val lootedItems = mutableListOf<LootItem>()
        for (item: GroundItem in lootItems) {
            if (Inventory.isFull()) break

            val inventoryBefore = Inventory.getAll().sumOf { it.stack }
            if (!item.interact("Take")) continue
            if (!Waiting.waitUntil(5000) { Inventory.getAll().sumOf { it.stack } > inventoryBefore }) continue

            val lootItem = LootItem(
                itemId = item.id,
                itemName = item.name,
                stack = item.stack,
            )
            lootedItems.add(lootItem)
            logger.debug("Looted item: ${item.name} x ${item.stack}")
            Waiting.waitNormal(88, 9)
        }

        return lootedItems
    }
}