package scripts.wrMotherlode.managers

import scripts.utils.Logger
import scripts.utils.failsafes.LastActionTracker

class StateManager(val logger: Logger) {
    private val states: MutableMap<String, Boolean> = mutableMapOf(
        "MINING" to false,
        "REPAIRING" to true,
        "FILLING" to true,
        "REPAIRING" to true,
        "COLLECTING" to true,
    )

    fun moveToNextState(): Boolean {
        return this.cycleStateFrom(
            this.getCurrentKey()
        )
    }

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
                logger.debug("Tripstate +1")
            }

            LastActionTracker.track("state")
            return true
        } else {
            logger.error("[State] - No state found...")
        }

        return false
    }

    fun resetCycle(to: String): Boolean {
        for (key in this.states.keys) {
            this.states[key] = true
        }

        this.states[to] = false

        return true
    }

}