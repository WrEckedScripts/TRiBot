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
import org.tribot.script.sdk.util.TribotRandom
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger
import kotlin.jvm.optionals.getOrNull

/**
 * The upkeep managers is used to keep track of when to pay the foreman
 * and to calculate / determine the next coffer amount to topup for
 * todo, idea to separate the foreman and the coffer into a separate manager.
 * - above lv 60, the foreman isn't needed anymore, so we should not need to load all this stuff in?
 */
class UpkeepManager(val logger: Logger) {
    var lastPaidForemanAt: Long? = null
    var nextCofferTopupAmount: Int = 0

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

    fun haveFilledCoffer(): Boolean {
        //todo don't wait until it's fully empty. randomly set new topup moments
        val cofferValue = GameState.getVarbit(5357)
        logger.error("CofferValue: ${cofferValue}")

        //todo, we don't want to wait until the coffer fully depleted,
        // so implement logic, to set a next topup at amount, similar to the nextCofferTopupAmount
        if (cofferValue <= 0) {
            setNextCofferTopup()
            return false
        }

        // coffer is not empty
        return true
    }

    fun getCofferTopupAmount(): Int {
        if (0 == nextCofferTopupAmount) {
            setNextCofferTopup()
        }

        return nextCofferTopupAmount;
    }

    fun setNextCofferTopup(): Unit {
        nextCofferTopupAmount = TribotRandom.uniform(10000, 20000)
        logger.error("Set next coffer topup amount to ${nextCofferTopupAmount}")
    }

    fun playerHoldsEnoughCoins(amount: Int = 2500): Boolean {
        logger.error("Checking if holding: '${amount}' coins")
        val coins = Query.inventory()
            .nameEquals("Coins")
            .minStack(amount)
            .findFirst()
            .getOrNull()

        logger.debug("Coin stack: ${coins?.stack}")

        if (null == coins) {
            return false
        }

        return true
    }
}