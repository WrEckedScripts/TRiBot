package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Skill
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.input.Keyboard
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.UpkeepManager

fun IParentNode.payForemanNode(logger: Logger, upkeepManager: UpkeepManager) = sequence {
    selector {
//        condition { Skill.SMITHING.actualLevel < 60 } //determine if we even need to pay the foreman
        perform {
            logger.error("PayForeManNode condition");
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

                    upkeepManager.setLastPaidForemanAt(System.currentTimeMillis())
                }
        }
    }
}