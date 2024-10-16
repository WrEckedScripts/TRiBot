package scripts.wrBlastFurnace.managers

import scripts.utils.Logger
import scripts.utils.calculators.CachedPerHourCalculator
import scripts.utils.formatters.Coins

/**
 * Manager class that keeps track of various progression related aspects
 * - Bars smelted
 * - Trips done
 * - Profit
 * - Experience
 */
class ProgressionManager(
    private val logger: Logger,
    private val startedAt: Long,
    private val tripStateManager: TripStateManager,
) {
    private val tripCalculator = CachedPerHourCalculator(this.startedAt)
    private val barCalculator = CachedPerHourCalculator(this.startedAt)

    fun indicateState(stateName: String): String {
        if (tripStateManager.isCurrentState(stateName) == false) {
            return "<---|"
        }

        return ""
    }

    fun currentTrips(): String {
        val value = Coins().format(tripStateManager.tripCount)
            .plus(" | ")
            .plus(" Bars ")
            .plus("(${Coins().format(tripStateManager.tripCount * tripStateManager.barsPerTrip)})")

        return value
    }

    private fun currentSpentValue(): Int {
        val secondariesSpent = tripStateManager.secondaryOre?.let { secondary ->
            val secondariesUsed = secondary.quantity() * tripStateManager.tripCount
            secondary.priceTimes(secondariesUsed)
        } ?: 0

        val baseUsed = tripStateManager.baseOre.quantity() * tripStateManager.tripCount
        val baseSpent = tripStateManager.baseOre.priceTimes(baseUsed)

        return secondariesSpent + baseSpent
    }

    fun currentSpent(): String {
        val raw = this.currentSpentValue()

        return "-".plus(Coins().format(raw))
    }

    private fun grossProfitValue(): Int {
        val barsCreated = tripStateManager.barsPerTrip * tripStateManager.tripCount
        return tripStateManager.meltableBar.bar().priceTimes(barsCreated)
    }

    fun grossProfit(): String {
        return Coins().format(
            this.grossProfitValue()
        )
    }

    fun netProfit(): String {
        val rawSum = this.grossProfitValue() - this.currentSpentValue()
        return Coins().format(rawSum)
    }

    fun estimatedPerHourTrips(): String {
        return "Trips ".plus(tripsPerHour())
            .plus(" p/hr")
            .plus(" | Bars ")
            .plus(barsPerHour())
            .plus(" p/hr")
    }

    private fun tripsPerHour(): String {
        return this.tripCalculator.perHour(0, tripStateManager.tripCount)
    }

    private fun barsPerHour(): String {
        return this.barCalculator.perHour(0, tripStateManager.tripCount * tripStateManager.barsPerTrip)
    }
}