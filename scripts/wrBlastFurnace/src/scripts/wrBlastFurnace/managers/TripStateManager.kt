package scripts.wrBlastFurnace.managers

import scripts.utils.Logger
import scripts.wrBlastFurnace.gui.Settings

class TripStateManager(val logger: Logger) {

    val meltableBar = Settings.barType!!
    val states = meltableBar.states
    val baseOre = meltableBar.baseOre()
    val secondaryOre = meltableBar.secondaryOre()

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