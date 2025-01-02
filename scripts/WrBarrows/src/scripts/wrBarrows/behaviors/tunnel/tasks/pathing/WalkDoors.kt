package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.types.GameObject
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container

// we might need another sort of class / object to keep track of walked doors in retries
class WalkDoors(val managers: Container) {
    private var walkedDoors: MutableList<GameObject> = mutableListOf()

    fun should(): Boolean {
        return true
    }

    fun execute(): Boolean {
        this.managers.tunnelManager.init()

        this.managers.tunnelManager.getDoors().forEach { door ->
            if (this.walkedDoors.contains(door)) {
                return@forEach
            }

            val succeeded = WalkDoor(door).execute()

            if (succeeded) {
                this.walkedDoors.add(door)
            }

            Waiting.wait(FatigueResolver.getMilliseconds())
        }

        return true
    }
}