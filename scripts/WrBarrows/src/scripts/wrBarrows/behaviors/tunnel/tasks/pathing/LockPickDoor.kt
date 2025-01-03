package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import org.tribot.script.sdk.types.InventoryItem
import kotlin.jvm.optionals.getOrNull

class LockPickDoor(val doorObject: GameObject) {
    fun satisfied(): Boolean {
        return false
    }

    fun execute(): Boolean {
        return this.getStrangeOldLockpick()?.useOn(
            this.doorObject
        ) ?: false
    }

    fun getStrangeOldLockpick(): InventoryItem? {
        return Query.inventory()
            .nameEquals("Strange old lockpick")
            .findFirst()
            .getOrNull()
    }
}