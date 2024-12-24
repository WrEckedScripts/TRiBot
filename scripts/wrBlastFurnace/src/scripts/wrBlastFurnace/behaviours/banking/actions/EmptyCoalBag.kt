package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBlastFurnace.managers.Container

fun IParentNode.emptyCoalBag(logger: Logger, managers: Container) = sequence {
    condition {
        if (managers.coalBagManager.getStateAsString() == "empty") {
            return@condition true
        }

        managers.repetitiveActionManager.increment("empty-coalbag", 6)

        Waiting.wait(FatigueResolver.getMilliseconds())

        val succeeded = Waiting.waitUntil(15_000) {
            Query.inventory()
                .nameEquals("Coal bag")
                .findFirst()
                .map { it.click("Empty") }

            // Slight wait, to prevent spam checking
            Waiting.wait(FatigueResolver.getMilliseconds())

            // Returns success/fail to the Waiting.
            Inventory.contains("Coal")
        }

        if (succeeded) {
            managers.repetitiveActionManager.reset("empty-coalbag")
        }

        succeeded
    }
}