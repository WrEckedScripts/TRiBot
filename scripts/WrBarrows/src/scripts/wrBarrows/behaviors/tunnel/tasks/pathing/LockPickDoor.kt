package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import org.tribot.script.sdk.types.InventoryItem
import scripts.wrBarrows.managers.Container
import kotlin.jvm.optionals.getOrNull

class LockPickDoor(val doorObject: GameObject, val managers: Container) {
    fun satisfied(): Boolean {
        return false
    }

    fun execute(): Boolean {
        val picked = this.getStrangeOldLockpick()?.useOn(
            this.doorObject
        ) ?: false

        this.managers.tunnelManager.unsetDoor(this.doorObject)

        return picked
    }

    fun getStrangeOldLockpick(): InventoryItem? {
        return Query.inventory()
            .nameContains("Strange old lockpick")
            .findFirst()
            .getOrNull()
    }
}