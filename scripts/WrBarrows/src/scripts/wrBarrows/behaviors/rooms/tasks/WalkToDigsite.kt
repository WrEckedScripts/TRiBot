package scripts.wrBarrows.behaviors.rooms.tasks

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container

class WalkToDigsite(val managers: Container) {
    fun satisfied(): Boolean {
        val targetDigsite = this.managers.roomManager.getTargetCrypt().room.area
        Logger("WalkToDigsite").debug("Target is: ${this.managers.roomManager.getTargetCrypt().room.name}")

        Logger("WalkToDigsite").debug("Target contains player -> ${targetDigsite.surface.containsMyPlayer()}")
        return targetDigsite.surface.containsMyPlayer()
    }

    fun execute(): Boolean {
        val targetLocation = this.managers.roomManager.getTargetCrypt().room.area

        val tile = targetLocation.surface.randomTile

        Logger("WalkToDigsite").debug("Walking towards digsite")

        // Default wait
        var wait = FatigueResolver.getMilliseconds()

        if (tile.isVisible && tile.isRendered) {
            tile.click()
            wait *= 4 // Slower on-screen clicking when tile is in range
        } else {
            LocalWalking.walkTo(targetLocation.surface.randomTile)
        }

        return Waiting.waitUntil(wait, wait / 2) {
            targetLocation.surface.containsMyPlayer()
        }
    }
}