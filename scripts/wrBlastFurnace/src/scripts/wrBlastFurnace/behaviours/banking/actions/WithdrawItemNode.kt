package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.banking.validation.ItemPresence

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
            var name = itemName

            // Randomize potion to pick, when working with a Stamina potion withdrawal
            // This ensures all doses are usable and are simply randomly picked.
            if (itemName.contains("potion")) {
                name = Query.bank()
                    .nameContains(itemName) // This still uses "Stamina potion"
                    .findRandom()
                    .get()
                    .name
            }

            // We will prevent early exiting, when running out of Stamina potions
            // But other items, need to be present, otherwise we will fail the script.
            //todo untested as of 1.3.0
            if (!name.contains("potion")) {
                ItemPresence.throwExceptionIfBankMissesItem(name)
            }

            // Either we need the exact item count
            val hasItemCount = Query.inventory()
                .nameEquals(name)
                .count() == quantity

            // Or, we need at least the quantity as a stack (for coins)
            val hasItemStack = Query.inventory()
                .nameEquals(name)
                .sumStacks() >= quantity

            var inventoryHasItem = hasItemCount || hasItemStack

            // Might ensure we have withdrawn before we close the bank.
            //TODO, re-write, do not assume that we've withdrawn before, in fact, utilize actual state based waiting
            // To ensure we double check to see if our inventory actually represents what we wanted to withdraw
            // - could be with a timeout checking, to give the withdraw some time to update game state
            // - ensure we query after withdrawing (as well) and not (only) before withdrawing like we do now.
            if (!inventoryHasItem) {
                inventoryHasItem = Waiting.waitUntil {
                    Bank.withdraw(name, quantity)
                }
            }

            //todo, what if we don't have the ores withdrawn?
            if (closeBankWindow) {
                Waiting.waitUntil {
                    Bank.close()
                }
            }

            Waiting.waitUntil {
                Waiting.waitNormal(450, 15)
                inventoryHasItem
            }
        }
    }
}