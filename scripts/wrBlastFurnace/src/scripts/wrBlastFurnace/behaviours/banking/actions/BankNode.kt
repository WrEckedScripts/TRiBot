package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.antiban.MiniBreak

/**
 * Node that should be called upon when we are within either one of the following area's
 * - Grand Exchange
 * - Blast Furnace
 * If we're not, we could always use the home teleport and start from lumbridge
 * - failsafe on the 30min timer, where we either log out and wait or start walking.
 *
 * This node is designed to ensure we're moving towards the bank chest / clerk and open the bank
 * This node DOES NOT interact any further with the bank. Additional nodes should be created for this.
 */
fun IParentNode.bankNode(
    logger: Logger,
    depositInventory: Boolean = false,
    close: Boolean = false
) = sequence {
    condition {
        if (depositInventory) {
            Bank.depositInventory()

            // Wait a maximum of 3 seconds to deposit our inventory
            Waiting.waitUntil(3000) {
                val deposited = Bank.depositInventory()
                Waiting.waitNormal(300, 30)
                deposited
            }

            Lottery.execute(0.06) {
                MiniBreak.leave()
            }

            // If somehow our inventory is still full, let's fail the condition.
            if (Inventory.isFull()) {
                logger.error("[Banking] - Failed to deposit inventory, re-trying")
                return@condition false
            }
        }

        return@condition true
    }
    condition {
        if (close) {
            Bank.close()
        }

        return@condition true
    }
}