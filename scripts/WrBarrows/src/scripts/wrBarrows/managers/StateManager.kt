package scripts.wrBarrows.managers

import scripts.utils.Logger
import scripts.utils.failsafes.LastActionTracker

class StateManager(val logger: Logger) {
    private val states: MutableMap<String, Boolean> = mutableMapOf(
//        State.PREPARE.name to false, TODO implement sequence
        State.ROOM.name to true,
        State.FIGHT.name to true,
        State.TUNNEL.name to false, //TODO due to start in tunnel missing some stuff.
        State.LOOT.name to true,
    )

    fun moveToNextState(): Boolean {
        return this.cycleStateFrom(
            this.getCurrentKey()
        )
    }

    fun isCurrentState(state: String): Boolean {
        return this.states[state] ?: true
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

    fun set(to: String): Boolean {
        this.logger.error("Changing state to: ${to}")
        for (key in this.states.keys) {
            this.states[key] = true
        }

        this.states[to] = false

        LastActionTracker.track("state")

        return true
    }

}