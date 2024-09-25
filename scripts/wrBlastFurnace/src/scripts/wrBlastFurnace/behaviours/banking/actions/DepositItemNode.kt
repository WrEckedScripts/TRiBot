package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger

/**
 * Node responsible for depositing a specific item and a quantity
 * useful for banking bars made, any other random items or other items we might want to bank.
 */
fun IParentNode.depositItemNode(
    logger: Logger,
    itemName: String? = null,
    quantity: Int? = null,
    closeBankWindow: Boolean = true
) = sequence {
    condition {
        // Allow full inventory depositing
        if (itemName == "" && !Inventory.isEmpty()) {
            Bank.depositInventory()
        }

        //todo, what happens if we have less items than the quantity?
        if (quantity != null) {
            logger.info("[depositItemNode] - Going to deposit ${itemName.toString()} x ${quantity}")
            Bank.deposit(itemName.toString(), quantity)
        }

        logger.info("[depositItemNode] - depositAll ${itemName.toString()}")
        Bank.depositAll(itemName.toString())

        if (closeBankWindow) {
            logger.info("[depositItemNode] - closing bankInterface")
            Bank.close()
        }

        true //todo, either properly populate a interacted variable or simply change to a perform()?
    }
}