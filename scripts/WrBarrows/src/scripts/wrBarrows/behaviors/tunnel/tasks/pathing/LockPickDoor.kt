package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import org.tribot.script.sdk.types.InventoryItem
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container
import kotlin.jvm.optionals.getOrNull

class LockPickDoor(val doorObject: GameObject, val managers: Container) {
    fun satisfied(): Boolean {
        return false
    }

    fun execute(): Boolean {
        val unlocked = this.getStrangeOldLockpick()?.useOn(
            this.doorObject
        ) ?: false

        val interacted = Waiting.waitUntil(15_000, FatigueResolver.getMilliseconds()) {
            Logger("DistanceLockPickDoor").warn("Distance to target: ${this.doorObject.distanceTo(MyPlayer.getTile())}")

            this.doorObject.distanceTo(MyPlayer.getTile()) <= 2
        }

        if (interacted) {
            this.managers.tunnelManager.unsetDoor(this.doorObject)
        }

        return unlocked && interacted
    }

    private fun getStrangeOldLockpick(): InventoryItem? {
        return Query.inventory()
            .nameContains("Strange old lockpick")
            .findFirst()
            .getOrNull()
    }
}