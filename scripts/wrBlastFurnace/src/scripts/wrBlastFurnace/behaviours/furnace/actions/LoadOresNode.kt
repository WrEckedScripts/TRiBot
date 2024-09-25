package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.BarManager

fun IParentNode.loadOresNode(logger: Logger, barManager: BarManager) = sequence {
    condition {
        var interacted = false
        logger.error("[loadOresNode] - Condition executed")
        val conveyor = Query.gameObjects()
            .nameEquals("Conveyor belt")
            .findBestInteractable()
            .get()

        // Prevents spam clicking while moving
        if (!MyPlayer.isMoving()) {
            interacted = Waiting.waitUntil {
                conveyor.interact("Put-ore-on")
            }
            Waiting.waitNormal(4120, 45)
        }

        logger.error("loadOresNode END result: ${Inventory.isEmpty() && !barManager.hasCollected()}")

        interacted
    }
}