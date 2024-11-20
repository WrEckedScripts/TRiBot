package scripts.wrBlastFurnace.managers

import scripts.utils.Logger
import scripts.wrBlastFurnace.gui.Settings

class TripStateManager(val logger: Logger) {
    var meltableBar = Settings.barType
    var states = meltableBar.states()
    var baseOre = meltableBar.baseOre()
    var secondaryOre = meltableBar.secondaryOre()

    var tripCount: Int = 0
    var barsPerTrip: Int = meltableBar.quantity()

    /**
     * Given this manager is initialised before the GUI. Let's allow reloading or parameters.
     * This way, any future GUI changes are properly reflected.
     */
    fun reload() {
        meltableBar = Settings.barType
        states = meltableBar.states()
        baseOre = meltableBar.baseOre()
        secondaryOre = meltableBar.secondaryOre()
        barsPerTrip = meltableBar.quantity()
    }

    fun isCurrentState(state: String): Boolean? {
        return states[state]
    }

    fun getCurrentKey(): String {
        return states.entries.first { !it.value }.key
    }

    fun cycleStateFrom(currentKey: String): Boolean {
        val keys = states.keys.toList()
        val currentIndex = keys.indexOf(currentKey)

        if (currentIndex != -1) {
            // Update the current item to true (processed)
            states[keys[currentIndex]] = true

            // Update the next item to false (to start processing it)
            val nextIndex = (currentIndex + 1) % keys.size
            states[keys[nextIndex]] = false

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
        for (key in states.keys) {
            states[key] = true
        }

        states[to] = false
    }
}