package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

/**
 * Only to be called after a bankNode() is called and finished.
 * This node withdraws a certain ore, based on what we pass through
 * - parameter ore: Should be from an Enum, but for now, let's use a string
 */
fun IParentNode.withdrawItemNode(
    logger: Logger,
    itemName: String,
    quantity: Int,
    closeBankWindow: Boolean = true
) = sequence {
    //If we run out of the specific ore we must go to the G.E. and buy them
    // we could also add a checkup logic to whenever we bank, that if we're running out of supplies
    // we start moving to the G.E. and re-supply our player
    // if we're at a buy-limit, we could take a break.

    selector {
        condition {
            // Either we need the exact item count
            val hasItemCount = Query.inventory()
                .nameEquals(itemName)
                .count() == quantity

            // Or, we need at least the quantity as a stack
            val hasItemStack = Query.inventory()
                .nameEquals(itemName)
                .sumStacks() >= quantity

            val inventoryHasItem = hasItemCount || hasItemStack

            var status = false

            // Might ensure we have withdrawn before we close the bank.
            if (!inventoryHasItem) {
                status = Waiting.waitUntil {
                    Bank.withdraw(itemName, quantity)
                }
            }

            if (status && closeBankWindow) {
                status = Waiting.waitUntil {
                    Bank.close()
                }
            }

            // Slight hack to ensure we return true/false
            status
        }
    }
}