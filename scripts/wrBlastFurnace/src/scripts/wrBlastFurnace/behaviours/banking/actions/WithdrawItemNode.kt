package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
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

    //TODO withdrawing can fail, if the inventory is still full, and coins didn't got withdrawn
    // - so this requires a failsafe checking
    selector {
        condition {
            Waiting.waitUntil {
                logger.debug("Withdrawing ${itemName} x ${quantity}")

                val status = Waiting.waitUntil {
                    Bank.withdraw(itemName, quantity)
                }

                if (closeBankWindow) {
                    Waiting.waitUntil {
                        Bank.close()
                    }
                }

                // Slight hack to ensure we return true/false
                // after the if statement if we didn't want to close the window
                logger.debug("what's the status? ${status}");
                status
            }
        }
    }
}