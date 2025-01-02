package scripts.wrBarrows.behaviors.rooms.tasks

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.behaviors.combat.tasks.DisablePrayer
import scripts.wrBarrows.managers.Container

class LeaveRoom(val managers: Container) {
    private val action: String = "Climb-up"
    private val objectName: String = "Staircase"

    fun should(): Boolean {
        //TODO, dictate the crypt we're in and if we should leave yet
        // having completed or marked the crypt as a tunnel?
        // whilst not moving towards the tunnel anyways (state = KILL?)

        return false
    }

    fun execute(): Boolean {
        DisablePrayer().execute()

        val exited = Waiting.waitUntil(25_000) {
            this.getStairs().interact(this.action)
        }

        Waiting.wait(FatigueResolver.getMilliseconds() * 2)

        Logger("LeaveRoom").debug("execute() - did we exit -> $exited")

        return exited
    }

    private fun getStairs(): GameObject {
        return Query.gameObjects()
            .nameEquals(this.objectName)
            .findBestInteractable()
            .get()
    }
}