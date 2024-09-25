package scripts.wrCrafting

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.BankSettings
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrCrafting.models.TaskConfiguration
import kotlin.random.Random

interface SuppliesManagerInterface {
    val taskConfiguration: TaskConfiguration
    val progressManager: ProgressManager

    fun logger(): Logger

    fun withdrawChiselFromBank(): Boolean

    fun withdrawCraftablesFromBank(): Boolean

    fun depositUnusableItems(): Boolean

    fun needsChisel(): Boolean

    fun needsCraftables(): Boolean

    fun hasCompletedInventory(): Boolean
}

class SuppliesManager(
    override val taskConfiguration: TaskConfiguration,
    override val progressManager: ProgressManager
) : SuppliesManagerInterface {

    override fun logger(): Logger {
        return Logger("Supplies")
    }

    override fun withdrawChiselFromBank(): Boolean {
        if (!Bank.isOpen()) {
            logger().error("[withdrawChiselFromBank] - can't, bank is not opened")
        }

        val bankHasChisel = Query.bank()
            .nameEquals("Chisel")
            .count() > 0

        val invHasChisel = Query.inventory()
            .nameEquals("Chisel")
            .count() > 0

        if (!bankHasChisel && !invHasChisel) {
            logger().error("[withdrawChiselFromBank] - we're out of chisels")
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
            logger().error("Oh oh, seems like the bank isn't open..")
        }

        val craftableCount: Int = Query.bank()
            .nameEquals(taskConfiguration.craftableName)
            .sumStacks()

        logger().debug(taskConfiguration.craftableName)

        logger().debug("[Stock count] - ${craftableCount}")

        if (craftableCount < 1) {
            logger().error("[withdrawCraftablesFromBank] - no craftables in bank")
        }

        val withdrawQuantity = if (craftableCount > 27) 27 else craftableCount
        logger().debug("Withdrawable amount : ${withdrawQuantity}")


        //TODO does this now, set the "x" value and simply left-clicks for re-stocking?
//        if (BankSettings.getWithdrawXQuantity() != withdrawQuantity) {
//            BankSettings.setWithdrawXQuantity(27)
//            logger().info("SET withdraw x quantity to '27'")
//        }

        Waiting.waitUntil {
            // DO NOT DO THE FOLLOWING, EVERYTIME:
            // - state based sleeping is more antiban than every iteration
            // - how about fatique, duration based delay in actions
            // - in short, doing a sleep after every craft cycle, is highly bannable
            /**
             * Something like (Antiban name is taken)
             * Antiban.executeAndWait(600, 9000) {
             *     logger().info("Antiban - Sleeping for ${wait}ms")
             *     Bank.withdraw(taskConfiguration.craftableName, withdrawQuantity)
             *     // under the hood, does this after the closure:
             *     Waiting.wait(RESULT_FROM_600_9000)
             * }
             */

            val wait = Random.nextInt(600, 9000)
            logger().info("Antiban - Sleeping for ${wait}ms")
            Bank.withdraw(taskConfiguration.craftableName, withdrawQuantity)
            //todo, for stuff like this, a manager/util class would be awesome
            Waiting.wait(wait)
            true
        }

        return true
    }

    override fun depositUnusableItems(): Boolean {
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
                logger().debug("Found unusable item: ${item.name}, time to bank them.")

                Bank.depositAll(item.name)

                // Make sure that the item is indeed banked, before moving to the next
                Waiting.waitUntil {
                    Query.inventory()
                        .nameEquals(item.name)
                        .findRandom()
                        .isEmpty
                }
            }

        //todo, is this the boogyman?
        Query.inventory()
            .nameEquals(
                "Chisel",
                taskConfiguration.craftableName
            )
            .isNoted
            .forEach { noted ->
                logger().debug("Found noted ${noted.name}, let's bank them.")
                Waiting.waitUntil {
                    Bank.depositAll(noted.name)
                }
            }

        return true
    }

    //TODO, if temp var + log, this keeps on getting executed.
    override fun needsChisel(): Boolean {
        return Query.inventory()
            .nameContains("Chisel")
            .isNotNoted //this should've fixed the always false loop... (was isNoted, dumbass....)
            .count() < 1
    }

    //TODO, if temp var + log, this keeps on getting executed.
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
        logger().debug("Checking inventory state: ${result}")
        return result == 0
    }

}