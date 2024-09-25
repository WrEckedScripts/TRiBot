package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.DispenserManager
import scripts.wrBlastFurnace.managers.TripStateManager
import scripts.wrBlastFurnace.managers.UpkeepManager

fun IParentNode.payForemanNode(
    logger: Logger,
    upkeepManager: UpkeepManager,
    tripStateManager: TripStateManager,
    dispenserManager: DispenserManager
) = sequence {
    selector {
        perform {
            Query.npcs()
                .nameEquals("Blast Furnace Foreman")
                .findBestInteractable()
                .map {
                    Waiting.waitUntil {
//                        it.adjustCameraTo()
                        it.interact("Pay")
                    }

                    Waiting.waitUntil {
                        ChatScreen.isOpen()
                    }

                    ChatScreen.handle("Yes")
                    upkeepManager.totalSpent += 2500

                    upkeepManager.setLastPaidForemanAt(System.currentTimeMillis())

                    logger.info("[Foreman] - dispenser: ${dispenserManager.holdsBars()}")
                    if (dispenserManager.holdsBars()) {
                        tripStateManager.resetCycle("COLLECT_BARS")
                    } else {
                        tripStateManager.resetCycle("PROCESS_COAL")
                    }
                }
        }
    }
}