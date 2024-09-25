package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.BarManager
import scripts.wrBlastFurnace.managers.TripStateManager
import kotlin.math.log

fun IParentNode.loadOresNode(
    logger: Logger,
    barManager: BarManager,
    tripStateManager: TripStateManager
) = sequence {
    condition {
        // TODO untested If we're here, with an empty inventory, we should skip.
        if (Inventory.isEmpty() && tripStateManager.isCurrentState("FILL_CONVEYOR") == false) {
            logger.error("INVENTORY EMPTY - Cycling state")
            tripStateManager.cycleStateFrom(
                tripStateManager.getCurrentKey()
            )
        } else {
            logger.error("[loadOresNode] - Condition executed")
            val conveyor = Query.gameObjects()
                .nameEquals("Conveyor belt")
                .findBestInteractable()
                .get()

            // Prevents spam clicking while moving
            if (!MyPlayer.isMoving()) {
                Waiting.waitUntil {
                    Waiting.waitNormal(900, 55)
                    conveyor.interact("Put-ore-on")
                }
            }

            //todo untested
            // Cycle from FILL_CONVEYOR to COLLECT_BARS
            tripStateManager.cycleStateFrom(
                "FILL_CONVEYOR"
            )

            Waiting.waitUntil {
                Waiting.waitNormal(2200, 55)
                tripStateManager.isCurrentState("COLLECT_BARS") == false
            }
        }
    }
}