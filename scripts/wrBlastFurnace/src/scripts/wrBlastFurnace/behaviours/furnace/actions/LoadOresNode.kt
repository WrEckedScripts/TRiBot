package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Tribot
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.antiban.Antiban
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.pricing.Pricing
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.wrBlastFurnace.managers.TripStateManager

fun IParentNode.loadOresNode(
    logger: Logger,
) = sequence {
    condition {
        Waiting.waitUntil {
            Waiting.waitNormal(675, 60)
            !Inventory.isEmpty()
        }

        val conveyor = Query.gameObjects()
            .nameEquals("Conveyor belt")
            .findBestInteractable()
            .get()

        val res = Waiting.waitUntil {
            Waiting.waitNormal(2000, 55)
            val interacted = conveyor.interact("Put-ore-on")

            //todo refactor to Antibanmanager class..
            Lottery(logger).execute(0.2) {
                val miniLeave = TribotRandom.normal(5000, 2340)
                logger.info("[Antiban] - Leaving screen for ${miniLeave}ms, we'll be right back.")
                Mouse.leaveScreen()
                Waiting.wait(miniLeave)
            }
            interacted
        }

        val inv = Waiting.waitUntil {
            Inventory.isEmpty()
        }

        logger.info("[Conveyor] - Interacted: ${res} - Inventory cleared - ${inv}")

        res && inv
    }
}