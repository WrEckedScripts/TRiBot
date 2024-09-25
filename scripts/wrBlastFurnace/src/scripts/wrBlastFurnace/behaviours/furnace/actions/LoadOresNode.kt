package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

fun IParentNode.loadOresNode(
    logger: Logger
) = sequence {
    condition {
        Waiting.waitUntil {
            Waiting.waitNormal(400, 60)
            !Inventory.isEmpty()
        }

        val conveyor = Query.gameObjects()
            .nameEquals("Conveyor belt")
            .findBestInteractable()
            .get()

        val res = Waiting.waitUntil {
            Waiting.waitNormal(1900, 55)
            conveyor.interact("Put-ore-on")
        }

        val inv = Waiting.waitUntil {
            Inventory.isEmpty()
        }

        logger.debug("LoadOres interaction - ${res} - ${inv}")

        //upon failure, we now re-bank.. this isn't necessary
        res && inv
    }
}