package scripts.utils.calculators

import kotlin.math.roundToInt

class TripCalculator(val startedAt: Long) {
    private var lastComputedValue: String? = null
    private var lastComputedTime: Long = 0
    private val computationIntervalMillis: Long = 60 * 500 // 0.5 minute


    fun perHour(start: Int, current: Int): String {
        val currentAt = System.currentTimeMillis()
        val elapsedTimeMillis = currentAt - this.startedAt

        // Return cached value if it's still valid
        if (elapsedTimeMillis < 1) {
            return "Calculating..."
        }

        if (lastComputedValue != null
            && currentAt - lastComputedTime < computationIntervalMillis
        ) {
            return lastComputedValue!!
        }

        // Calculate per hour value
        val gained = current - start
        val elapsedTimeHours = elapsedTimeMillis / 3600000.0
        val perHour = gained / elapsedTimeHours

        // Cache the computed value
        lastComputedValue = perHour.roundToInt().toString()
        lastComputedTime = currentAt

        return lastComputedValue!!
    }
}
