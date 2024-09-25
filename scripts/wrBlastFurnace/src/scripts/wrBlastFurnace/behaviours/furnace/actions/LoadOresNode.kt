package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.BarManager
import scripts.wrBlastFurnace.managers.TripStateManager

fun IParentNode.loadOresNode(
    logger: Logger,
    barManager: BarManager,
    tripStateManager: TripStateManager
) = sequence {
    condition {
        logger.error("[loadOresNode] - Condition executed")
        val conveyor = Query.gameObjects()
            .nameEquals("Conveyor belt")
            .findBestInteractable()
            .get()

        // Prevents spam clicking while moving
        if (!MyPlayer.isMoving()) {
            Waiting.waitUntil {
                conveyor.interact("Put-ore-on")
            }
            Waiting.waitNormal(2300, 45)
        }

        Waiting.waitUntil {
            Waiting.waitNormal(2400, 55)
            tripStateManager.isCurrentState("COLLECT_BARS") == false
        }

        tripStateManager.cycleStateFrom(
            tripStateManager.getCurrentKey()
        )
    }
}