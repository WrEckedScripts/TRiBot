package scripts.wrCrafting

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

interface SuppliesManagerInterface {
    val logger: Logger
    val craftableName: String

    fun withdrawChiselFromBank(): Boolean

    fun withdrawCraftablesFromBank(): Boolean

    fun needsChisel(): Boolean

    fun needsCraftables(): Boolean

    fun hasCompletedInventory(): Boolean
}

class SuppliesManager(
    override val logger: Logger,
    override val craftableName: String
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
        Bank.withdraw("Chisel", 1)

        return true
    }

    override fun withdrawCraftablesFromBank(): Boolean {
        if (!Bank.isOpen()) {
            logger.error("[withdrawCraftablesFromBank] - can't bank is not open")
        }

        val craftableCount: Int = Query.bank()
            .nameEquals(craftableName)
            .sumStacks()

        logger.debug("[SUM] - ${craftableCount}")

        if (craftableCount < 1) {
            logger.error("[withdrawCraftablesFromBank] - no craftables in bank")
        }

        val withdrawQuantity = if (craftableCount > 27) 27 else craftableCount
        logger.debug("[withdrawQuantity] = ${withdrawQuantity}")

        Bank.withdraw(craftableName, withdrawQuantity)

        return true
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
            .nameEquals(craftableName)
            .isNotNoted
            .count() < 1
    }

    override fun hasCompletedInventory(): Boolean {
        val result = Query.inventory()
            .nameEquals(craftableName)
            .isNotNoted
            .count()

        return result == 0
    }

}