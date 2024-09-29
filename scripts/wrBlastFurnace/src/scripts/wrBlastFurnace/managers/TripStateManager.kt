package scripts.wrBlastFurnace.managers

import scripts.utils.Logger
import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore

class TripStateManager(val logger: Logger) {
    /**
     * List of states, that reflect the trips taken for Steel bars
     * - false = active
     * - true = inactive
     *
     * Since the behaviour tree loves fulfilling conditions.
     */
    private val states = mutableMapOf(
        "PROCESS_COAL" to false,
        "PROCESS_BASE" to true,
        "COLLECT_BARS" to true,
        "BANK_BARS" to true
    )

    val baseOre = Ore("Iron ore", 28, 440)
    val coalOre = Ore("Coal", 28, 453)
    val bar = Bar("Steel bar", 2353)

    var tripCount: Int = 0
    var barsPerTrip: Int = 28

    fun isCurrentState(state: String): Boolean? {
        return this.states[state]
    }

    fun getCurrentKey(): String {
        return this.states.entries.first { !it.value }.key
    }

    fun cycleStateFrom(currentKey: String): Boolean {
        val keys = this.states.keys.toList()
        val currentIndex = keys.indexOf(currentKey)

        if (currentIndex != -1) {
            // Update the current item to true (processed)
            this.states[keys[currentIndex]] = true

            // Update the next item to false (to start processing it)
            val nextIndex = (currentIndex + 1) % keys.size
            this.states[keys[nextIndex]] = false

            if (nextIndex == 0) {
                this.tripCount++
            }

            return true
        } else {
            logger.error("[State] - No state found...")
        }

        return false
    }

    fun resetCycle(to: String) {
        for (key in this.states.keys) {
            this.states[key] = true
        }

        this.states[to] = false
    }
}