package scripts.wrCannonBalls.behaviours.banking

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.behaviours.banking.validation.ItemPresence
import scripts.utils.calculators.ResourceCounter
import scripts.utils.failsafes.LastActionTracker
import scripts.wrCannonBalls.managers.Container

fun IParentNode.ensureSmeltReadyInventory(
    logger: Logger,
    managers: Container
) = sequence {
    condition {
        ResourceCounter.set("Cannonball", Inventory.getCount("Cannonball"))

        if (!CannonballInventory().ready()) {
            LastActionTracker.track("state")
            LastActionTracker.track("click")

            ItemPresence.throwExceptionIfBankMissesItem("Steel bar")

            CannonballInventory().builder().execute()

            ResourceCounter.increment("Trips")
            ResourceCounter.increment(
                "Steel bar",
                Inventory.getCount("Steel bar")
            )
        }

        Waiting.waitUntil(FatigueResolver.getMilliseconds()) {
            CannonballInventory().ready()
        }
    }
}