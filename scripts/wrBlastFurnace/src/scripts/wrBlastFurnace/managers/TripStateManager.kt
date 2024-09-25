package scripts.wrBlastFurnace.managers

import scripts.utils.Logger

class TripStateManager(val logger: Logger) {
    val bronzeStates = mutableMapOf(
        "COLLECT_ORES" to false,
        "FILL_CONVEYOR" to true,
        "COLLECT_BARS" to true,
        "BANK_BARS" to true
    )

    var tripCount: Int = 0

    fun isCurrentState(state: String): Boolean? {
        return this.bronzeStates[state]
    }

    fun getCurrentKey(): String {
        return this.bronzeStates.entries.first { !it.value }.key
    }

    fun cycleStateFrom(currentKey: String): Boolean {
        val keys = this.bronzeStates.keys.toList()
        val currentIndex = keys.indexOf(currentKey)

        if (currentIndex != -1) {
            // Update the current item to true (processed)
            this.bronzeStates[keys[currentIndex]] = true

            // Update the next item to false (to start processing it)
            val nextIndex = (currentIndex + 1) % keys.size
            this.bronzeStates[keys[nextIndex]] = false

            if(nextIndex == 0){
                this.tripCount++
            }

            return true
        } else {
            logger.error("[TripStateManager] - No state found...")
        }

        return false
    }

    fun resetCycle(to: String){
        for (key in this.bronzeStates.keys){
            this.bronzeStates[key] = true
        }

        this.bronzeStates[to] = false
    }
}