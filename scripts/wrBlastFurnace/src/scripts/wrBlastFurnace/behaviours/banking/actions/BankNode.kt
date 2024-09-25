package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Inventory
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
fun IParentNode.bankNode(
    logger: Logger,
    depositInventory: Boolean = false,
    close: Boolean = false
    ) = sequence {
    selector {
        // If the bank is not open
        condition {
            logger.info("selector:bankNode | ensuring open state")
            Bank.isOpen()
        }
        // Walk to the nearest bank, when there's a bank nearby
        sequence {
            selector {
                condition {
                    logger.info("selector:bankNode | ensuring nearby")
                    Bank.isNearby()
                }
                condition {
                    logger.info("selector:bankNode | walkToTheBank")
                    GlobalWalking.walkToBank()
                }
            }
            condition {
                logger.info("selector:bankNode | ensure open")
                Bank.ensureOpen()
            }
            //TODO, change this to a condition, to prevent trying to take bars with full inv of ores for example.
            perform {
                if (depositInventory) {
                    logger.error("- -selector:bankNode | deposit whole inventory")
                    Bank.depositInventory()
                }

                if(close){
                    Bank.close()
                }
            }
        }

    }
}