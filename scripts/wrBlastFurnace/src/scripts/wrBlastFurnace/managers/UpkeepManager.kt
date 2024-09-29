package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Skill
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger
import kotlin.jvm.optionals.getOrNull

/**
 * The upkeep manager is used to keep track of when to pay the foreman and to fill up the coffer
 * And we do keep track of the coins spent on up-keeping here.
 * Additionally, any next occurrence calculations can be found here for continuously having a filled up coffer.
 */
class UpkeepManager(val logger: Logger) {
    private var nextCofferTopupAmount: Int = 0
    private var nextCofferTopupThreshold: Int = 0

    var lastPaidForemanAt: Long? = null
    var totalSpent = 0

    fun setLastPaidForemanAt(timestamp: Long) {
        lastPaidForemanAt = timestamp
    }

    fun shouldPayForeman(): Boolean {
        return Skill.SMITHING.currentLevel < 60
    }

    fun havePaidForeman(): Boolean {
        if (!this.shouldPayForeman()) {
            lastPaidForemanAt = null
            return true
        }

        // if the lastPaidForemanAt = 10 minutes ago, return true
        val lastPaidAt = lastPaidForemanAt ?: return false // if null, we should pay them now.
        val currentTimestamp = System.currentTimeMillis()

        val randAdd = TribotRandom.normal(400, 55)
        val nextTimeInMillis = 60 * (9_000 + randAdd) // 9 minutes in miliseconds + some random
        // if the difference, between the current timestamp, minus the last paid at
        // is lower than 10 minutes, we don't need to pay the foreman
        return (currentTimestamp - lastPaidAt) <= nextTimeInMillis
    }

    private fun getCofferValue(): Int {
        return GameState.getVarbit(5357)
    }

    fun haveFilledCoffer(): Boolean {
        val cofferValue = this.getCofferValue()

        if (cofferValue <= this.nextCofferTopupThreshold) {
            getCofferTopupAmount()
            return false
        }

        return true
    }

    /**
     * Randomizes the values for next topup amount and threshold to fill it at.
     */
    fun getCofferTopupAmount(): Int {
        if (0 == this.nextCofferTopupAmount) {
            setNextCofferTopup()
        }

        if (0 == this.nextCofferTopupThreshold) {
            setNextCofferTopupThreshold()
        }

        return this.nextCofferTopupAmount
    }

    /**
     * To prevent always waiting until the coffer is fully depleted, give it some wiggle room
     * - Could in case of a overlapping coffer values cause a double top-up
     * - - Which, imo, could serve as mimicking human behaviour.
     */
    fun setNextCofferTopupThreshold() {
        this.nextCofferTopupThreshold = TribotRandom.uniform(0, 15000)
    }

    fun setNextCofferTopup(): Unit {
        //todo user pref / gui option
        val minAmount = 10000// 10k
        val maxAmount = 200000// 200k
        val stepSize = 1000// 1k

        this.nextCofferTopupAmount = TribotRandom.uniform(
            minAmount / stepSize, // 10k becomes 10
            maxAmount / stepSize // 200k becomes 200
        ) * stepSize // Example: 164 * 1000 = 164k
    }

    fun playerHoldsEnoughCoins(amount: Int = 2500): Boolean {
        val coins = Query.inventory()
            .nameEquals("Coins")
            .minStack(amount)
            .findFirst()
            .getOrNull()

        return null != coins
    }
}