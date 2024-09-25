package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.wrBlastFurnace.managers.BarManager
import scripts.wrBlastFurnace.managers.TripStateManager

fun IParentNode.collectBarsNode(
    logger: Logger,
    barManager: BarManager,
    tripStateManager: TripStateManager
) = sequence {
    condition {
        val inventoryContainsBars = Query.inventory()
            .nameContains("bar")
            .count() > 0

        if (inventoryContainsBars) {
            tripStateManager.cycleStateFrom(
                tripStateManager.getCurrentKey()
            )
        } else {
            val dispenser = Query.gameObjects()
                .nameEquals("Bar dispenser")
                .findBestInteractable()
                .get()

            // Randomize pre-hover
//            Lottery(logger).execute(0.6) {
//                Mouse.
//            }

            // Wait until the bars are ready
            Waiting.waitUntil {
                Waiting.waitNormal(400, 55)
                barManager.dispenserHoldsBars()
            }

            Waiting.waitUntil {
                Waiting.waitNormal(800, 45)
                dispenser.interact("Take")
            }

            Waiting.waitUntil {
                MakeScreen.isOpen()
                Waiting.waitNormal(300, 86)

                // What about a random/player pref space bar spam
                // needs to ensure we got make-all set.
                MakeScreen.makeAll(tripStateManager.bar)

                Query.inventory()
                    .nameContains("bar")
                    .count() > 0
            }

            val invCount = Query.inventory()
                .nameContains("bar")
                .count()

            logger.debug("collecting bars x ${invCount}")

            tripStateManager.cycleStateFrom(
                "COLLECT_BARS"
            )
        }
    }
}