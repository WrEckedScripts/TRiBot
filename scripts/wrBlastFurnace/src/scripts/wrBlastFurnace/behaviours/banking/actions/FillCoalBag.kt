package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

fun IParentNode.fillCoalBag(logger: Logger) = sequence {
    condition {
        logger.debug("[1] - FILLING COALBAG")
        // In between world hops etc, this prevents early exit-ing the script
        Waiting.waitNormal(650, 40)

        Waiting.waitUntil(4_000) {
            Query.inventory()
                .nameEquals("Coal bag")
                .findFirst()
                .map { it.click("Fill") }
            logger.debug("[2] - COALBAG FILLED")

            // Slight wait, to prevent spam checking
            Waiting.waitNormal(700, 50)

            // Returns success/fail to the Waiting.
            Inventory.contains("Coal bag")
        }
    }
}