package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.antiban.MiniBreak
import scripts.utils.failsafes.RepetitiveActionManager

fun IParentNode.loadOresNode(
    logger: Logger,
    repetitiveActionManager: RepetitiveActionManager
) = sequence {
    condition {
        repetitiveActionManager.increment("load-ores", 15)

        Waiting.waitUntil {
            Waiting.waitNormal(475, 60)
            !Inventory.isEmpty()
        }

        val conveyor = Query.gameObjects()
            .nameEquals("Conveyor belt")
            .findBestInteractable()

        val res = Waiting.waitUntil(TribotRandom.normal(1750, 55)) {
            if (Inventory.isEmpty()) {
                return@waitUntil true
            }

            val interacted = conveyor.map { belt ->
                belt.interact("Put-ore-on")
            }.orElse(false)

            if (!interacted) {
                logger.error("[Failure] - Failed to interact with 'conveyor', we will re-try")
            }

            // Handles the "first-time" putting ores on the conveyor.
            if (ChatScreen.isOpen()) {
                ChatScreen.selectOption(
                    "Yes, and don't ask again."
                )
            }

            Lottery.execute(0.16) {
                MiniBreak.leave()
            }

            return@waitUntil interacted
        }

        val inv = Waiting.waitUntil {
            Waiting.waitNormal(1200, 120)
            Inventory.isEmpty()
        }

        if (res && inv) {
            repetitiveActionManager.reset("load-ores")
        }

        res && inv
    }
}