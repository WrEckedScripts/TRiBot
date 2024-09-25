package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Skill
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
    private var nextCofferTopupAmount: Int = 0
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

    fun haveFilledCoffer(): Boolean {
        //todo don't wait until it's fully empty. randomly set new topup moments
        val cofferValue = GameState.getVarbit(5357)

        //todo, we don't want to wait until the coffer fully depleted,
        // so implement logic, to set a next topup at amount, similar to the nextCofferTopupAmount
        if (cofferValue <= 0) {
            getCofferTopupAmount()
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
        val minAmount = 10000// 10k
        val maxAmount = 200000// 200k
        val stepSize = 1000// 1k

        this.nextCofferTopupAmount = TribotRandom.uniform(
            minAmount / stepSize, // 10k becomes 10
            maxAmount / stepSize // 200k becomes 200
        ) * stepSize // Example: 164 * 1000 = 164k

        Waiting.waitNormal(400, 45) // todo might fix resulting in 0 value in withdrawNode
    }

    fun playerHoldsEnoughCoins(amount: Int = 2500): Boolean {
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