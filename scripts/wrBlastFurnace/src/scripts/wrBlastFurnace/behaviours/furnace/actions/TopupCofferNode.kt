package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.EnterInputScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.UpkeepManager

fun IParentNode.topupCofferNode(
    logger: Logger,
    upkeepManager: UpkeepManager,
) = sequence {
    // keep track of this, and whenever we top-up the coffer, let's pick a next at random number
    // as well as the amount per topup.
    perform {
        logger.debug("Performing topupCofferNode")
        Query.gameObjects()
            .nameEquals("Coffer")
            .findFirst()
            .map {
                Waiting.waitUntil {
                    it.adjustCameraTo()
                    it.interact("Use")
                }

                Waiting.waitUntil {
                    ChatScreen.isOpen()
                }

                //this can fail, if not paid foreman
                ChatScreen.handle("Deposit Coins.")

                Waiting.waitUntil {
                    EnterInputScreen.isOpen()
                    EnterInputScreen.enter(upkeepManager.getCofferTopupAmount())
                }

                upkeepManager.totalSpent += upkeepManager.getCofferTopupAmount()
                logger.info("[UpkeepManagement] - Topped up the Coffer with: ${upkeepManager.getCofferTopupAmount()} coins")

                Waiting.waitUntil {
                    ChatScreen.isClickContinueOpen()
                }

                Waiting.waitNormal(1100, 120)
                ChatScreen.clickContinue()
                Waiting.waitNormal(900, 120)
            }
    }

}