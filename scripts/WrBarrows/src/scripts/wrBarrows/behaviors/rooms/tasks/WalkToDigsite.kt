package scripts.wrBarrows.behaviors.rooms.tasks

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.walking.LocalWalking
import scripts.wrBarrows.managers.Container

class WalkToDigsite(val managers: Container) {
    fun satisfied(): Boolean {
        val targetDigsite = this.managers.roomManager.getTargetCrypt().room.area
        return targetDigsite.surface.containsMyPlayer()
    }

    fun execute(): Boolean {
        val targetLocation = this.managers.roomManager.getTargetCrypt().room.area

        Waiting.waitUntil(20_000) {
            LocalWalking.walkTo(targetLocation.surface.randomTile)
        }

        return targetLocation.surface.containsMyPlayer()
    }
}