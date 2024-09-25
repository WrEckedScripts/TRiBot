package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.DispenserManager
import scripts.wrBlastFurnace.managers.TripStateManager

fun IParentNode.collectBarsNode(
    logger: Logger,
    dispenserManager: DispenserManager,
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
//            Lottery.execute(0.6) {
//                dispenser.hover()
//            }

            Waiting.waitUntil {
                val holdsBars = dispenserManager.holdsBars()

                if (!holdsBars) {
                    Waiting.waitNormal(2062, 254)
                }

                Waiting.waitNormal(150, 75)
                holdsBars
            }

            Waiting.waitUntil {
                val interacted = dispenser.interact("Take")

                if (!interacted) {
                    logger.error("Failed to interact with dispenser")
                }

                // Wait until game updates moving state
                if (interacted) {
                    Waiting.waitUntil { MyPlayer.isMoving() }
                }

                // Have some patience on the player moving to the dispenser.
                if (interacted && MyPlayer.isMoving()) {
                    Waiting.waitUntil {
                        Waiting.waitNormal(500, 50)
                        !MyPlayer.isMoving()
                    }
                }

                interacted && MakeScreen.isOpen()
            }

            // MakeScreen is open, move onto making the actual bars
            // We'll give this action 5 seconds to finish, otherwise we'd failed and need to redo the condition.
            Waiting.waitUntil(5000) {
                MakeScreen.makeAll(tripStateManager.bar.name())

                val barsInInventoryCount = Query.inventory()
                    .nameContains("bar")
                    .count()

                val succeeded = barsInInventoryCount > 0

                if (!succeeded) {
                    // Slight delay before we re-run this waiting lambda.
                    // Could fix any game loading delays, where the inventory wasn't updated yet.
                    Waiting.waitNormal(175, 45)
                }

                succeeded
            }

            val barsInInventoryCount = Query.inventory()
                .nameContains("bar")
                .count()

            if (barsInInventoryCount == 0) {
                logger.error("[CollectBars] - We've failed to collect bars from the dispenser, re-trying...")
                return@condition false
            }

            tripStateManager.cycleStateFrom(
                tripStateManager.getCurrentKey()
            )
        }
    }
}