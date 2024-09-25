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
        val value = tripStateManager.tripCount.toString()
            .plus(" | ")
            .plus(" Bars ")
            .plus("(${tripStateManager.tripCount * tripStateManager.barsPerTrip})")

        return value
    }

    private fun currentSpentValue(): Int {
        val coalsUsed = tripStateManager.coalOre.quantity() * tripStateManager.tripCount
        val baseUsed = tripStateManager.baseOre.quantity() * tripStateManager.tripCount

        val coalSpent = tripStateManager.coalOre.priceTimes(coalsUsed)
        val baseSpent = tripStateManager.baseOre.priceTimes(baseUsed)

        return coalSpent + baseSpent
    }

    fun currentSpent(): String {
        val raw = this.currentSpentValue()

        return "-".plus(Coins().format(raw))
    }

    private fun grossProfitValue(): Int {
        val barsCreated = tripStateManager.barsPerTrip * tripStateManager.tripCount
        return tripStateManager.bar.priceTimes(barsCreated)
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