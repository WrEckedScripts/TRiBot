package scripts.wrSkilling.managers

import scripts.utils.Logger

class StateManager(val logger: Logger) {

    val crafterStates: MutableMap<String, Boolean> = mutableMapOf(
        "WITHDRAW_SUPPLIES" to false,
        "CRAFT_LEATHER" to true,
        "WAITING" to true,
    )

    val lighterStates: MutableMap<String, Boolean> = mutableMapOf(
        "WITHDRAW_SUPPLIES" to false,
        "LIGHTING_BONFIRE" to true,
        "FEED_BONFIRE" to true,
        "WAITING" to true,
    )

    private var states = crafterStates

    // lil low-level way of re-using this stateManager between the different trees
    fun changeStates(name: String) {
        if (name == "craft") {
            this.states = crafterStates
        }

        if (name == "fm") {
            this.states = lighterStates
        }
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