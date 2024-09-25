package scripts.wrBlastFurnace.managers

import scripts.utils.Logger
import scripts.utils.calculators.CachedPerHourCalculator

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
    private val barManager: BarManager
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