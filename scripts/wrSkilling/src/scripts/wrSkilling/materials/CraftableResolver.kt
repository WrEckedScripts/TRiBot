package scripts.wrSkilling.materials

import org.tribot.script.sdk.Skill

object CraftableResolver {
    private val itemPerLevel: Map<LeatherItem, Pair<Int, Int>> = LeatherItem.values().associateWith {
        Pair(it.minLevel, it.maxLevel)
    }

    fun getItem(): String {
        val level = Skill.CRAFTING.actualLevel

        val item = itemPerLevel.entries.first {
            // Check if our level, falls in between the pair's range
            level in it.value.first until it.value.second
        }.key

        return item.itemName
    }
}