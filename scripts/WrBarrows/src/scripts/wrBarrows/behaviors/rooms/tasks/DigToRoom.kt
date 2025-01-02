package scripts.wrBarrows.behaviors.rooms.tasks

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container
import kotlin.jvm.optionals.getOrNull

class DigToRoom(val managers: Container) {
    fun satisfied(): Boolean {
        val digSite = this.managers.roomManager.getTargetCrypt().room.area.surface
        val standingInDigsite = digSite.containsMyPlayer()

        Logger("DigToRoom").debug("satisfied() - Are we at diggin area? -> $standingInDigsite")
        return !standingInDigsite
    }

    fun execute(): Boolean {
        val spade = Query.inventory()
            .nameContains("Spade")
            .findFirst()
            .getOrNull()

        Logger("DigToRoom").debug("execute() - Got a spade let's dig!?")
        spade?.click("Dig") ?: false

        val isInsideCrypt = Waiting.waitUntil(15_000, FatigueResolver.getMilliseconds()) {
            this.managers.roomManager.getCurrentCrypt()?.value?.room?.area?.crypt?.containsMyPlayer() ?: false
        }

        return isInsideCrypt
    }
}