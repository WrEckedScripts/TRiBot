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
        Waiting.wait(200) // Give the game a slight break to ensure we're not too fast.

        ItemPresence.throwExceptionIfBankMissesItem("Coal", 27)
        managers.repetitiveActionManager.increment("fill-coalbag", 6)

        Waiting.waitUntil(4_000) {
            val filled = Query.inventory()
                .nameEquals("Coal bag")
                .findFirst()
                .map { it.click("Fill") }
                .orElse(false)

            // Slight wait, to prevent spam checking/clicking
            Waiting.wait(FatigueResolver.getMilliseconds())

            // No idea if it's even possible to get the contents
            // And since it can only click fill if it's fillable
            // and given we know for sure we are holding the bag in inventory at this point.
            // in case this returns false, it couldn't click the fill
            // I assume its filled.
            if (filled == false) {
                logger.debug("Coal bag is already filled")
                return@waitUntil true
            }

            logger.warn("Coal bag filled-state: $filled")
            managers.repetitiveActionManager.reset("fill-coalbag")
            filled
        }
    }
}