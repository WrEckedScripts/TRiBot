package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver

fun IParentNode.emptyCoalBag(logger: Logger) = sequence {
    // In between world hops etc, this prevents early exit-ing the script
    Waiting.waitNormal(650, 40)

    condition {
        Waiting.waitUntil(3_000) {
            Query.inventory()
                .nameEquals("Coal bag")
                .findFirst()
                .map { it.click("Empty") }

            // Slight wait, to prevent spam checking
            Waiting.wait(FatigueResolver.getMilliseconds())

            // Returns success/fail to the Waiting.
            Inventory.contains("Coal")
        }
    }
}