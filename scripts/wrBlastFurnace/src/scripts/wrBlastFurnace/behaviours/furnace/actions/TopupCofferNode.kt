package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.EnterInputScreen
import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.input.Keyboard
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.managers.UpkeepManager

fun IParentNode.topupCofferNode(
    logger: Logger,
    upkeepManager: UpkeepManager
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

                logger.info("Interacted with the coffer via 'Use'")

                Waiting.waitUntil {
                    ChatScreen.isOpen()
                }

                logger.info("ChatScreen opened")

                //this can fail, if not paid foreman
                ChatScreen.handle("Deposit Coins.")

                Waiting.waitUntil {
                    EnterInputScreen.isOpen()
                    EnterInputScreen.enter(upkeepManager.getCofferTopupAmount())
                }

                logger.info("Entered into inputScreen: ${upkeepManager.getCofferTopupAmount()}")

                Waiting.waitUntil {
                    ChatScreen.isClickContinueOpen()
                }

                //todo this could use some sleeping, now it's instantly ran once the clickContinue is shown
                ChatScreen.clickContinue()

                Waiting.wait(4000)
            }
    }

}