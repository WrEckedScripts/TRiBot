package scripts.wrBarrows.behaviors.tunnel.tasks.pathing

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.behaviors.combat.tasks.CheckIAmUnderAttack
import scripts.wrBarrows.behaviors.tunnel.tasks.looting.LootChest
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State
import java.awt.Color
import java.awt.Graphics

class LockPickDoors(val managers: Container) {
    fun satisfied(): Boolean {
        return this.managers.tunnelManager.getDoors().isEmpty()
    }

    fun execute(): Boolean {
        val doorsIterator = this.managers.tunnelManager.getDoors().iterator()

        while (doorsIterator.hasNext()) {
            val door = doorsIterator.next()

            Painting.addPaint { g: Graphics ->
                g.color = Color.GREEN
                door.allTiles.forEach {
                    g.drawPolygon(it.bounds.get())
                }
            }

            val isReachable = Waiting.waitUntil(5_000) {
                this.isReachable(door.tile)
            }

            if (!isReachable) {
                Logger("LockPickDoors").warn("Unreachable door, skipping")
                doorsIterator.remove()
                continue
            }

            LockPickDoor(door).execute()

            Waiting.waitUntilAnimating(5_000)

            doorsIterator.remove() // Remove directly from the iterator

            Waiting.waitUntil(2_500) {
                CheckIAmUnderAttack(managers).execute()
            }
        }

        Waiting.wait(FatigueResolver.getMilliseconds() / 2)

        if (LootChest(managers).isInsideLootChamber()) {
            this.managers.stateManager.set(State.LOOT.name)
        }

        return false
    }

    private fun isReachable(tile: WorldTile): Boolean {
        val path = LocalWalking.Map.builder().source(MyPlayer.getTile())
            .build()

        Painting.addPaint { g: Graphics ->
            g.color = Color.GREEN

            path.getPath(tile).forEach {
                g.drawPolygon(it.tile.bounds.get())
            }
        }

        return path.getPath(tile).isNotEmpty()
    }
}