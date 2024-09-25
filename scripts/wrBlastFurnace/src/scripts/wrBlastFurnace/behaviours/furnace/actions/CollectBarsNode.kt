package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.BarManager

fun IParentNode.collectBarsNode(
    logger: Logger,
    barManager: BarManager
) = sequence {
    perform {
        val dispenser = Query.gameObjects()
            .nameEquals("Bar dispenser")
            .findBestInteractable()
            .get()

        Waiting.waitUntil {
            logger.error("taking the bars!")
            Waiting.waitNormal(1200,45)
            dispenser.interact("Take")
        }

        Waiting.waitUntil {
            MakeScreen.isOpen()
        }

        Waiting.waitUntil {
            MakeScreen.makeAll("Bronze bar")
        }
    }
}