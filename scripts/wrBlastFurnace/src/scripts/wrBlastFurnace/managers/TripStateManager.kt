package scripts.wrBlastFurnace.managers

import scripts.utils.Logger
import scripts.wrBlastFurnace.banking.materials.Ore

class TripStateManager(val logger: Logger) {
    val states = mutableMapOf(
        "PROCESS_COAL" to false,
        "PROCESS_BASE" to true,
        "COLLECT_BARS" to true,
        "BANK_BARS" to true
    )

    val baseOre = Ore("Iron ore", 28)
    val coalOre = Ore("Coal", 28)
    val bar = "Steel bar"

    var tripCount: Int = 0
    var barsPerTrip: Int = 28

    // Keep track of costs and gross profit
    var oreSpent: Int = 0;
    var barProfit: Int = 0;

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
            logger.error("[State] - cycling state from ${currentKey} to ${nextIndex}")

            if (nextIndex == 0) {
                this.tripCount++
            }

            return true
        } else {
            logger.error("[TripStateManager] - No state found...")
        }

        return false
    }

    fun resetCycle(to: String) {
        logger.error("[State] - RESET state to: ${to}")
        for (key in this.states.keys) {
            this.states[key] = true
        }

        this.states[to] = false
    }
}