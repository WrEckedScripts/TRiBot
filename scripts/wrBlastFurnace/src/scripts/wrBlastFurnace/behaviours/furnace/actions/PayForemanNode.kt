package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.TripStateManager
import scripts.wrBlastFurnace.managers.UpkeepManager

fun IParentNode.payForemanNode(
    logger: Logger,
    upkeepManager: UpkeepManager,
    tripStateManager: TripStateManager
) = sequence {
    selector {
//        condition { Skill.SMITHING.actualLevel < 60 } //determine if we even need to pay the foreman
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

                    // Ensure we reset our cycle after we've done upkeeing with the foreman
                    tripStateManager.resetCycle("COLLECT_ORES")
                }
        }
    }
}