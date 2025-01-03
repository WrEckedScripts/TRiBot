package scripts.wrBarrows.behaviors.tunnel.tasks.looting

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.GameObject
import org.tribot.script.sdk.types.WorldTile
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.behaviors.combat.tasks.CheckIAmUnderAttack
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.player.BarrowsBrother
import kotlin.jvm.optionals.getOrNull

class LootChest(val managers: Container) {
    fun satisfied(): Boolean {
        return !this.isInsideLootChamber()
    }

    fun execute(): Boolean {
        // BEWARE: a Brother can spawn (if any are unkilled TODO check for that)
        // new brotherManager that keeps track of this?
        val unkilledBrother = BarrowsBrother.values().firstOrNull {
            it.varbit.get() == 0
        }

        // First "Open" the "Chest"
        Waiting.waitUntil(15_000, FatigueResolver.getMilliseconds() * 3) {
            this.chestObject()?.interact("Open") ?: false
        }

        Logger("LootChest").debug("UnkilledBrother: $unkilledBrother")

        if (null != unkilledBrother) {
            Waiting.waitUntil(6_000, 150) {
                CheckIAmUnderAttack(managers).execute()
            }
        }

        // Then, "Search" the "Chest" to actually loot
        Waiting.waitUntil(15_000, FatigueResolver.getMilliseconds()) {
            this.chestObject()?.interact("Search") ?: false
        }

        //Wait a brief moment, screenshot and calculate loot!

        Waiting.wait(5_000) //todo slight wait for ourselfs.

        return Waiting.waitUntil {
            this.managers.roomManager.refresh()
            Logger("LootChest").debug("Fake teleporting")

            Waiting.wait(FatigueResolver.getMilliseconds())

            Logger("LootChest").debug("Fake true")
            true
        }
    }

    fun isInsideLootChamber(): Boolean {
        val area = Area.fromPolygon(
            WorldTile(3546, 9702, 0),
            WorldTile(3545, 9689, 0),
            WorldTile(3558, 9689, 0),
            WorldTile(3558, 9700, 0)
        )

        return area.containsMyPlayer()
    }

    fun chestObject(): GameObject? {
        return Query.gameObjects()
            .nameEquals("Chest")
            .findBestInteractable()
            .getOrNull()
    }
}