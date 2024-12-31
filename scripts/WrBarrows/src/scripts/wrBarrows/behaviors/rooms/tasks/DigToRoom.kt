package scripts.wrBarrows.behaviors.rooms.tasks

import org.tribot.script.sdk.query.Query
import scripts.wrBarrows.managers.Container

class DigToRoom(val managers: Container) {
    fun satisfied(): Boolean {
        val digSite = this.managers.roomManager.getTargetCrypt().room.area.surface
        val standingInDigsite = digSite.containsMyPlayer()
        return standingInDigsite
    }

    fun execute(): Boolean {
        val spade = Query.inventory()
            .nameContains("Spade")
            .findFirst()
            .get()

        return spade.click("Dig")
    }
}