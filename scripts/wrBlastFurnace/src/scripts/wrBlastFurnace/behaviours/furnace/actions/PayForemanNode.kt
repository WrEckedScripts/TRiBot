package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Skill
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.BarManager
import scripts.wrBlastFurnace.managers.TripStateManager
import scripts.wrBlastFurnace.managers.UpkeepManager

fun IParentNode.payForemanNode(
    logger: Logger,
    upkeepManager: UpkeepManager,
    tripStateManager: TripStateManager,
    barManager: BarManager
) = sequence {
    selector {
        condition { Skill.SMITHING.actualLevel < 60 }
        perform {
            Query.npcs()
                .nameEquals("Blast Furnace Foreman")
                .findBestInteractable()
                .map {
                    Waiting.waitUntil {
                        it.adjustCameraTo()
                        it.interact("Pay")
                    }

                    Waiting.waitUntil {
                        ChatScreen.isOpen()
                    }

                    ChatScreen.handle("Yes")
                    upkeepManager.totalSpent += 2500

                    upkeepManager.setLastPaidForemanAt(System.currentTimeMillis())

                    logger.info("[Foreman] - dispenser: ${barManager.dispenserHoldsBars()}")
                    if (barManager.dispenserHoldsBars()) {
                        tripStateManager.resetCycle("COLLECT_BARS")
                    } else {
                        tripStateManager.resetCycle("PROCESS_COAL")
                    }
                }
        }
    }
}