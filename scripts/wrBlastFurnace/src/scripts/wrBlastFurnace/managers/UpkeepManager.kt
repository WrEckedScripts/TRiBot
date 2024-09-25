package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.Camera
import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.input.Keyboard
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger
import kotlin.jvm.optionals.getOrNull

/**
 * The upkeep managers is used to keep track of when to pay the foreman
 * and to calculate / determine the next coffer amount to topup for
 *
 */
class UpkeepManager(val logger: Logger) {
    var lastPaidForemanAt: Long? = null

    fun setLastPaidForemanAt(timestamp: Long) {
        lastPaidForemanAt = timestamp
    }

    fun havePaidForeman(): Boolean {
        // if the lastPaidForemanAt = 10 minutes ago, return true
        val lastPaidAt = lastPaidForemanAt ?: return false // if null, we should pay them now.
        logger.error("lastPaidAt: ${lastPaidAt}")
        val currentTimestamp = System.currentTimeMillis()

        val tenMinutesInMillis = 10 * 60 * 1000 // 10 minutes in miliseconds

        // if the difference, between the current timestamp, minus the last paid at
        // is lower than 10 minutes, we don't need to pay the foreman
        logger.debug("shouldPay")
        logger.debug("Comparison ${(currentTimestamp - lastPaidAt)} < ${tenMinutesInMillis}")
        return (currentTimestamp - lastPaidAt) <= tenMinutesInMillis
    }

    fun shouldTopupCoffer(): Boolean {
        //todo don't wait until it's fully empty. randomly set new topup moments
        val cofferValue = GameState.getVarbit(5357)

        if (cofferValue <= 0) {
            return false
        }

        // coffer is not empty
        return true
    }

    fun playerHoldsEnoughCoins(): Boolean {
        val coins = Query.inventory()
            .nameEquals("Coins")
            .minStack(2500)
            .findFirst()
            .getOrNull()

        logger.debug("Coins: ${coins}")

        if (null == coins) {
            logger.debug("returning false")
            return false
        }

        logger.debug("returning true")
        return true
    }
}