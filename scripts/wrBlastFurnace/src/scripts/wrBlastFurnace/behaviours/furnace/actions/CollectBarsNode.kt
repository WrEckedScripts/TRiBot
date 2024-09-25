package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
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
                Waiting.waitNormal(2262, 254)
                barManager.dispenserHoldsBars()
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

            Waiting.waitUntil {
                // What about a random/player pref space bar spam
                // needs to ensure we got make-all set.
                MakeScreen.makeAll(tripStateManager.bar)

                val barsInInventoryCount = Query.inventory()
                    .nameContains("bar")
                    .count()

                val succeeded = barsInInventoryCount > 0

                if (!succeeded) {
                    Waiting.waitNormal(174, 28)
                }

                succeeded
            }

            val invCount = Query.inventory()
                .nameContains("bar")
                .count()

            if (invCount == 0) {
                logger.debug("collecting bars x ${invCount}")
                logger.error("Failed to make bars?!")

                return@condition false
            }

            tripStateManager.cycleStateFrom(
                tripStateManager.getCurrentKey()
            )
        }
    }
}