package scripts.wrBlastFurnace.managers

import scripts.utils.Logger
import scripts.utils.calculators.CachedPerHourCalculator
import scripts.utils.formatters.Notator

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

    fun barsLabel(): String {
        val current = tripStateManager.tripCount.times(tripStateManager.barsPerTrip)
        val formatted = Notator.format(current)
        val perHour = this.barCalculator.perHour(0, current)

        return formatted
            .plus(" (")
            .plus(perHour)
            .plus(" p/h)")
    }

    fun tripsLabel(): String {
        val current = tripStateManager.tripCount
        val formatted = Notator.format(current)
        val perHour = this.tripCalculator.perHour(0, current)

        return formatted
            .plus(" (")
            .plus(perHour)
            .plus(" p/h)")
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

        return "-".plus(Notator.format(raw))
    }

    private fun grossProfitValue(): Int {
        val barsCreated = tripStateManager.barsPerTrip * tripStateManager.tripCount
        return tripStateManager.meltableBar.bar().priceTimes(barsCreated)
    }

    fun grossProfit(): String {
        return Notator.format(
            this.grossProfitValue()
        )
    }

    fun netProfit(): String {
        val rawSum = this.grossProfitValue() - this.currentSpentValue()
        return Notator.format(rawSum)
    }
}