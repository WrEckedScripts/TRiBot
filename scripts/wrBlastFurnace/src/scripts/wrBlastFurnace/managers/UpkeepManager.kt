package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.util.TribotRandom
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

    var totalSpent = 0

    fun setLastPaidForemanAt(timestamp: Long) {
        lastPaidForemanAt = timestamp
    }

    fun havePaidForeman(): Boolean {
        // if the lastPaidForemanAt = 10 minutes ago, return true
        val lastPaidAt = lastPaidForemanAt ?: return false // if null, we should pay them now.
        val currentTimestamp = System.currentTimeMillis()

        val randAdd = TribotRandom.normal(400,55)
        val nextTimeInMillis = 60 * (9_000 + randAdd) // 9 minutes in miliseconds + some random

        logger.error("foreman timer state: ${(currentTimestamp - lastPaidAt) <= nextTimeInMillis}")
        // if the difference, between the current timestamp, minus the last paid at
        // is lower than 10 minutes, we don't need to pay the foreman
        return (currentTimestamp - lastPaidAt) <= nextTimeInMillis
    }

    fun haveFilledCoffer(): Boolean {
        //todo don't wait until it's fully empty. randomly set new topup moments
        val cofferValue = GameState.getVarbit(5357)

        logger.error("doubletake: ${nextCofferTopupAmount} | this.${this.nextCofferTopupAmount}")

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
        if (0 == this.nextCofferTopupAmount) {
            setNextCofferTopup()
        }

        return this.nextCofferTopupAmount
    }

    fun setNextCofferTopup(): Unit {
        this.nextCofferTopupAmount = TribotRandom.uniform(10000, 200000)
        Waiting.waitNormal(400,45) // todo ight fix resulting in 0 value in withdrawNode
    }

    fun playerHoldsEnoughCoins(amount: Int = 2500): Boolean {
        logger.error("Checking if holding: '${amount}' coins")
        val coins = Query.inventory()
            .nameEquals("Coins")
            .minStack(amount)
            .findFirst()
            .getOrNull()

        if (null == coins) {
            return false
        }

        return true
    }
}