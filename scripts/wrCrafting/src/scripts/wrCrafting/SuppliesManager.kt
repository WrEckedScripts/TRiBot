package scripts.wrCrafting

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrCrafting.models.TaskConfiguration

interface SuppliesManagerInterface {
    val logger: Logger
    val taskConfiguration: TaskConfiguration
    val progressManager: ProgressManager

    fun withdrawChiselFromBank(): Boolean

    fun withdrawCraftablesFromBank(): Boolean

    fun depositUnusableItems()

    fun needsChisel(): Boolean

    fun needsCraftables(): Boolean

    fun hasCompletedInventory(): Boolean
}

class SuppliesManager(
    override val logger: Logger,
    override val taskConfiguration: TaskConfiguration,
    override val progressManager: ProgressManager
) : SuppliesManagerInterface {

    override fun withdrawChiselFromBank(): Boolean {
        if (!Bank.isOpen()) {
            logger.error("[withdrawChiselFromBank] - can't, bank is not opened")
        }

        val bankHasChisel = Query.bank()
            .nameEquals("Chisel")
            .count() > 0

        if (!bankHasChisel) {
            logger.error("[withdrawChiselFromBank] - we're out of chisels")
            //todo buy one
        }

        //todo, check what happens if "note" is selected.
        //todo BankSettings entity!
        Waiting.waitUntil {
            Bank.withdraw("Chisel", 1)
        }

        return true
    }

    override fun withdrawCraftablesFromBank(): Boolean {
        if (!Bank.isOpen()) {
            logger.error("[withdrawCraftablesFromBank] - can't bank is not open")
        }

        val craftableCount: Int = Query.bank()
            .nameEquals(taskConfiguration.craftableName)
            .sumStacks()

        logger.debug("[SUM] - ${craftableCount}")

        if (craftableCount < 1) {
            logger.error("[withdrawCraftablesFromBank] - no craftables in bank")
        }

        val withdrawQuantity = if (craftableCount > 27) 27 else craftableCount
        logger.debug("[withdrawQuantity] = ${withdrawQuantity}")


        // TODO could also setup the script to change the button click to simply click gems to fill the full inv
        Waiting.waitUntil {
            Bank.withdraw(taskConfiguration.craftableName, withdrawQuantity)
        }

        return true
    }

    override fun depositUnusableItems() {
        val processedCount = Query.inventory()
            .nameEquals(taskConfiguration.craftedName)
            .isNotNoted
            .count()

        progressManager.incrementCraftedItems(processedCount)

        Query.inventory()
            .nameNotEquals(
                "Chisel",
                taskConfiguration.craftableName
            )
            .forEach { item ->
                logger.debug("Found unusable item: ${item.name}, time to bank them.")
                // Per unusable item, let's deposit-all
                Bank.depositAll(item.name)

                // Make sure that the item is indeed banked, before moving to the next
                Waiting.waitUntil {
                    Query.inventory()
                        .nameEquals(item.name)
                        .findRandom()
                        .isEmpty
                }
            }

        //todo any noted craftableItem should also be banked
        Query.inventory()
            .nameEquals(
                "Chisel",
                taskConfiguration.craftableName
            )
            .isNoted
            .forEach { noted ->
                logger.debug("Found noted ${noted.name}, let's bank them.")
                Waiting.waitUntil {
                    Bank.depositAll(noted.name)
                }
            }
    }

    override fun needsChisel(): Boolean {
        return !Query.inventory()
            .nameContains("Chisel")
            .isNotNoted //this should've fixed the always false loop... (was isNoted, dumbass....)
            .findRandom()
            .isPresent
    }

    override fun needsCraftables(): Boolean {
        return Query.inventory()
            .nameEquals(taskConfiguration.craftableName)
            .isNotNoted
            .count() < 1
    }

    //TODO, not used, as we're keeping the state
    // but let's keep it for now.
    override fun hasCompletedInventory(): Boolean {
        val result = Query.inventory()
            .nameEquals(taskConfiguration.craftableName)
            .isNotNoted
            .count()

        return result == 0
    }

}