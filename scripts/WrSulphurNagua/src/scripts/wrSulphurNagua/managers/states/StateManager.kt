package scripts.wrSulphurNagua.managers.states

import scripts.utils.failsafes.LastActionTracker

class StateManager {
    // Active state is indicated by "false" value
    private val states: MutableMap<String, Boolean> = mutableMapOf(
        State.PREPARATION.name to false,
        State.COMBAT.name to true,
        State.BANKING.name to true,
        State.LOOTING.name to true
    )

    /**
     * Returns a boolean to indicate if we're handling the state or not
     */
    fun isState(state: String): Boolean {
        return this.states[state] ?: true
    }

    /**
     * Returns the current state
     */
    fun getCurrent(): String {
        return this.states.entries.first { !it.value }.key
    }

    /**
     * Set our active state to the supplied state name
     */
    fun set(to: String): Boolean {
        // Disable all states
        for (key in this.states.keys) {
            this.states[key] = true
        }

        // Enable specified State as active
        this.states[to] = false

        LastActionTracker.track("state")

        return true
    }

    /**
     * @deprecated Rarely if not ever using this, so I might just ditch this code.
     * And only use explicit state persistence.
     *
     * Move towards the next state, based off of the current state
     */
    fun next(current: String): Boolean {
        val keys = this.states.keys.toList()
        val index = keys.indexOf(current)

        if (index != -1) {
            // Update the current item to true (processed)
            this.states[keys[index]] = true

            // Update the next item to false (to start processing it)
            val nextIndex = (index + 1) % keys.size
            this.states[keys[nextIndex]] = false

            LastActionTracker.track("state")
            return true
        }

        return false
    }
}