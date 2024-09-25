package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
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
    itemName: String,
    quantity: Int? = null,
    closeBankWindow: Boolean = true
) = sequence {
    condition {
        //todo, what happens if we have less items than the quantity?
        if (quantity != null){
            Bank.deposit(itemName, quantity)
        }

        Bank.depositAll(itemName)

        if (closeBankWindow) {
            Bank.close()
        }

        true
    }
}