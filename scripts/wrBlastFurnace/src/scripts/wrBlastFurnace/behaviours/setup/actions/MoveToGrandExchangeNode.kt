package scripts.wrBlastFurnace.behaviours.setup.actions

import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import java.util.logging.Logger

/**
 * Node that ensures we move our player towards the grand exchange
 * This can be via various ways.
 * At first i'd say we should start somewhere near a bank and call this node
 * to let this node grab either a varrock tele or a ring of wealth (x)
 *
 */
fun IParentNode.moveToGrandExchangeNode(logger: Logger) = sequence {
    //
}