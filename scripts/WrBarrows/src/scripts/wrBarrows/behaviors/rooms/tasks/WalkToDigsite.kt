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

        if (tile.isVisible && tile.isRendered) {
            tile.click()
        } else {
            LocalWalking.walkTo(targetLocation.surface.randomTile)
        }

        Waiting.wait(FatigueResolver.getMilliseconds() * 5)

        return targetLocation.surface.containsMyPlayer()
    }
}