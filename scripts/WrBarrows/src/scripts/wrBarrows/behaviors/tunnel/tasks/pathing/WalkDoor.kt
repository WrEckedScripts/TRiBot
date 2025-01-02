package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import org.tribot.script.sdk.types.GameObject

class WalkDoor(val doorObject: GameObject) {
    private val action: String = "Open"
    fun should(): Boolean {
        return true
    }

    fun execute(): Boolean {
        return this.doorObject.interact(this.action)
    }
}