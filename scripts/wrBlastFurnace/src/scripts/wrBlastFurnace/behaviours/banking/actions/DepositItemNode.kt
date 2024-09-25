package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
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
        var status = false

        val inventoryIsBanked = Query.inventory()
            .nameContains(itemName)
            .count() == 0

        if (inventoryIsBanked) {
            status = true
        }

        // Allow full inventory depositing
        if (!status &&
            itemName == "" &&
            !Inventory.isEmpty()
        ) {
            status = Bank.depositInventory()
        }

        //todo, what happens if we have less items than the quantity?
        if (!status && quantity != null) {
            status = Bank.deposit(itemName.toString(), quantity)
        }

        if (!status && quantity.toString() != "null") {
            status = Bank.depositAll(itemName.toString())
        }

        if (status &&
            inventoryIsBanked &&
            closeBankWindow
        ) {
            logger.info("[depositItemNode] - closing bankInterface")
            Bank.close()
        }

        status && inventoryIsBanked
    }
}