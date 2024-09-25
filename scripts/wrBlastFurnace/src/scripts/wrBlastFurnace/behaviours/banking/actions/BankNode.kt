package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger

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
fun IParentNode.bankNode(logger: Logger) = sequence {
    selector {
        // If the bank is not open
        condition {
            logger.debug("BANKOPENCLOSED: ${Bank.isOpen()}")
            Bank.isOpen()
        }
        // Walk to the nearest bank, when there's a bank nearby
        sequence {
            selector {
                condition {
                    logger.debug("BankState = ${Bank.isNearby()}")
                    Bank.isNearby()
                }
                condition {
                    GlobalWalking.walkToBank()
                }
            }
            condition {
                Bank.ensureOpen()
            }
        }

    }
}