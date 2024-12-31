package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.InventoryItem
import kotlin.jvm.optionals.getOrNull

class ConsumePotion(val undosedName: String) {
    fun satisfied(): Boolean {
        return true
    }

    fun execute(): Boolean {
        if (null == this.getPotionItem()) {
            // missing potion
            return false
        }

        return this.getPotionItem()!!.click()
    }

    private fun getPotionItem(): InventoryItem? {
        return Query.inventory()
            .nameContains(undosedName)
            .findRandom()
            .getOrNull()
    }
}