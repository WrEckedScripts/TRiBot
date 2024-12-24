package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.behaviours.banking.validation.ItemPresence
import scripts.wrBlastFurnace.managers.Container

fun IParentNode.fillCoalBag(
    logger: Logger,
    managers: Container
) = sequence {
    condition {
        if (managers.coalBagManager.getStateAsString() == "filled") {
            return@condition true
        }

        Waiting.wait(FatigueResolver.getMilliseconds() / 2) // Give the game a slight break to ensure we're not too fast.

        ItemPresence.throwExceptionIfBankMissesItem("Coal", 27)
        managers.repetitiveActionManager.increment("fill-coalbag", 6)

        Waiting.waitUntil(15_000) {
            Waiting.wait(FatigueResolver.getMilliseconds())

            Query.inventory()
                .nameEquals("Coal bag")
                .findFirst()
                .map { it.click("Fill") }
                .orElse(false)
        }
    }
}