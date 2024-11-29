package scripts.wrCannonBalls.behaviours.banking

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.debug.LastActionTracker
import scripts.wrCannonBalls.managers.Container
import scripts.wrCannonBalls.overlay.ResourceCounter

fun IParentNode.ensureSmeltReadyInventory(
    logger: Logger,
    managers: Container
) = sequence {
    condition {
        ResourceCounter.set("Cannonball", Inventory.getCount("Cannonball"))

        logger.warn(CanSmeltBalls().builder().isSatisfied())
        if (!CanSmeltBalls().ready()) {
            logger.warn("Inventory isn't satisfied, executing task.")
            LastActionTracker.track("state")
            LastActionTracker.track("click")

            CanSmeltBalls().builder().execute()

            ResourceCounter.increment("Trips")
            ResourceCounter.increment(
                "Steel bar",
                Inventory.getCount("Steel bar")
            )
        }

        Waiting.waitUntil(FatigueResolver.getMilliseconds()) {
            CanSmeltBalls().ready()
        }
    }
}