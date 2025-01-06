package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.InventoryItem
import kotlin.jvm.optionals.getOrNull

class ConsumeFood {
    fun should(): Boolean {
        val satisfies = this.satisfiesHitpoints()

        return satisfies
    }

    fun execute(): Boolean {
        if (null == this.getFood()) {
            // TODO we're out of food
            return false
        }

        return this.getFood()!!.click("Eat")
    }

    private fun satisfiesHitpoints(): Boolean {
        return MyPlayer.getCurrentHealthPercent() >= 70
    }

    private fun getFood(): InventoryItem? {
        return Query.inventory()
            .nameContains("Shark")
            .findRandom()
            .getOrNull()
    }
}