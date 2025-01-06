package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State
import scripts.wrBarrows.tunnel.NextDoorResolver

class LockPickDoors(val managers: Container) {
    fun satisfied(): Boolean {
        return this.managers.tunnelManager.getDoors().isEmpty()
    }

    fun execute(): Boolean {
        val doors = this.managers.tunnelManager.getDoors()
        val resolver = NextDoorResolver(doors)

        val handledDoor = LockPickDoor(resolver.nextReachableDoor(), this.managers).execute()

        if (!handledDoor) {
            return false
        }

        if (this.managers.tunnelManager.insideChestRoom()) {
            this.managers.stateManager.set(State.LOOT.name)
        }

        return this.managers.tunnelManager.insideChestRoom()
    }
}