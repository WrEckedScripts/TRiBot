package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger

fun IParentNode.topupCofferNode(logger: Logger) = sequence {
    // keep track of this, and whenever we top-up the coffer, let's pick a next at random number
    // as well as the amount per topup.
    val coffer = GameState.getVarbit(5357)

    logger.debug(coffer)
    Waiting.wait(5000)
}