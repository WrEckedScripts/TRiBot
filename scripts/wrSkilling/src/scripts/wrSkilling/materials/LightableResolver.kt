package scripts.wrSkilling.materials

import org.tribot.script.sdk.Skill

object LightableResolver {
    private val itemPerLevel: Map<LightableItem, Pair<Int, Int>> = LightableItem.values().associateWith {
        Pair(it.minLevel, it.maxLevel)
    }

    fun getItem(): String {
        val level = Skill.FIREMAKING.actualLevel

        val item = itemPerLevel.entries.first {
            level in it.value.first until it.value.second
        }.key

        return item.itemName
    }
}