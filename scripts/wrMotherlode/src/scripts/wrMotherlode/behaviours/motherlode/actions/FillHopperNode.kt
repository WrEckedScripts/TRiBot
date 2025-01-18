package scripts.wrMotherlode.behaviours.motherlode.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.failsafes.LastActionTracker
import scripts.wrMotherlode.banking.actions.MineReadyInventoryBuilder
import scripts.wrMotherlode.managers.Container
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

fun IParentNode.fillHopperNode(
    logger: Logger,
    managers: Container
) = sequence {
    condition {
        managers.repetitiveActionManager.increment("fill-hopper", 4)

        // Walk towards the hopper, if we should
        val hopperTile = WorldTile(3750, 5673, 0)
        if (!hopperTile.isVisible || !hopperTile.isOnMinimap) {
            Waiting.waitUntil(15_000) {
                LocalWalking.walkTo(hopperTile)

                MyPlayer.getTile() != hopperTile.tile
            }
        }

        // Fill the hopper
        val filled = Waiting.waitUntil(15_000) {
            Query.gameObjects()
                .nameEquals("Hopper")
                .findBestInteractable()
                .getOrNull()?.interact("Deposit")

            LastActionTracker.track("click")

            Lottery.execute(Random.nextDouble(0.80, 0.95)) {
                Mouse.leaveScreen()
            }

            // Prevent spam clicks
            Waiting.waitNormal(10_500, 2_123)

            // If we've got remaining pay-dirt, once the sack is full
            // ensure we drop the remainder
            // todo, curious if this is correctly calculated once the wheels aren't spinning..
            //  we could also listen for the chat..
            if (!managers.sackManager.canBeFilled()) {
                MineReadyInventoryBuilder().clearPayDirt()
            }

            Inventory.getCount(12011) == 0
        }

        if (filled) {
            managers.repetitiveActionManager.reset("fill-hopper")
        }

        filled
    }
    // Ensure we've dropped off our inventory
    condition {
        !Inventory.contains("Pay-dirt")
    }
}