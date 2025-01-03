package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import org.tribot.script.sdk.Waiting
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.behaviors.combat.tasks.CheckIAmUnderAttack
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

        LockPickDoor(resolver.furthestReachableDoor(), managers).execute()
        Waiting.wait(FatigueResolver.getMilliseconds() * 5)

        Waiting.waitUntil(FatigueResolver.getMilliseconds() * 3) {
            CheckIAmUnderAttack(managers).execute()
        }

        if (managers.tunnelManager.insideChestRoom()) {
            this.managers.stateManager.set(State.LOOT.name)
        }

        return false
    }
}