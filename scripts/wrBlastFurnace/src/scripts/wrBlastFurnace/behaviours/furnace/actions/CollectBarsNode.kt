package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.perform
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
    perform {
        val inventoryContainsBars = Query.inventory()
            .nameContains("bar")
            .count() > 0

        logger.debug("InventoryContainsBars ${inventoryContainsBars}")

        if (inventoryContainsBars) {
            // TODO, didn't happen anymore, but let's keep this for now
            logger.error("We've collected the bars, cycling state now.")
            tripStateManager.cycleStateFrom(
                tripStateManager.getCurrentKey()
            )
        } else {
            val dispenser = Query.gameObjects()
                .nameEquals("Bar dispenser")
                .findBestInteractable()
                .get()

            // Wait until the bars are ready
            Waiting.waitUntil {
                Waiting.waitNormal(600, 55)
                !barManager.dispenserHoldsBars()
            }

            Waiting.waitUntil {
                Waiting.waitNormal(800, 45)
                dispenser.interact("Take")
            }

            Waiting.waitUntil {
                MakeScreen.isOpen()
            }

            Waiting.waitUntil {
                Waiting.waitNormal(700, 86)
                MakeScreen.makeAll("Bronze bar")
            }

            Waiting.waitUntil {
                Query.inventory()
                    .nameContains("bar")
                    .count() > 0
            }

            tripStateManager.cycleStateFrom(
                "COLLECT_BARS"
            )
        }
    }
}