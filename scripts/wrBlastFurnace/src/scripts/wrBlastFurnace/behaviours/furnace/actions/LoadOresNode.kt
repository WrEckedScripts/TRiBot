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
            logger.debug("[1] - LOADORES - ${Inventory.getFilledSlots()}")
            Waiting.waitNormal(475, 60)
            Inventory.getFilledSlots() > 1
        }

        val conveyor = Query.gameObjects()
            .nameEquals("Conveyor belt")
            .findBestInteractable()

        logger.debug("[2] - LOADORES - conveyorPresent: ${conveyor.isPresent}")

        val res = Waiting.waitUntil(TribotRandom.normal(1750, 55)) {
            if (Inventory.getFilledSlots() < 2) {
                logger.debug("[3] - LOADORES - slots below 2 ({${Inventory.getFilledSlots()}}) returning 'TRUE'")
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
            Inventory.getFilledSlots() == 1
        }

        logger.warn("[4] - ending: inv:${inv} | ${Inventory.getFilledSlots()} | res:${res}")

        if (res && inv) {
            repetitiveActionManager.reset("load-ores")
        }

        res && inv
    }
}