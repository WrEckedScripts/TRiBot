package scripts.utils.failsafes

import scripts.utils.Logger

/**
 * @experimental Needs thorough validation and implementation
 *
 * This manager can be used to create on-demand counters to increment per action.
 * We can specify max attempts and act accordingly when a threshold is reached.
 *
 * The max attempts represent the times a certain, repetitive action occurs due to failures.
 * We must, upon success reset the counter.
 */
class RepetitiveActionManager(
    private val logger: Logger
) {
    // A map to hold all the counters with named keys
    private val counters: MutableMap<String, Int> = mutableMapOf()

    // A map to keep track of all the thresholds, per counter, with named keys
    private val maxAttempts: MutableMap<String, Int> = mutableMapOf()

    /**
     * Method to create a new counter, or, if a counter already exists, returns the existing counter's value
     */
    fun create(name: String, attempts: Int = 5): Int {
        return if (counters.containsKey(name)) {
            this.logger.info("[Failsafe] - Already got a counter for ${name} holding ${counters[name]} attempts")
            counters[name] ?: 0
        } else {
            this.logger.info("[Failsafe] - New counter for ${name}")
            counters[name] = 0
            maxAttempts[name] = attempts
            0 // Returns the initial 0 attempts
        }
    }

    fun increment(name: String, attempts: Int = 5): Boolean {
        // Ensure we got an existing counter, by either resolving or creating one.
        this.create(name, attempts)

        val currentCount = counters[name] ?: 0
        val threshold = maxAttempts[name] ?: 0

        return if (currentCount < threshold) {
            this.logger.info("[Failsafe] - incrementing ${name} to ${currentCount + 1} attempts")
            counters[name] = currentCount + 1
            true
        } else {
            throw RuntimeException("Repetitive failure to execute task, shutting down to prevent grabbing attention..")
        }
    }

    fun reset(name: String) {
        if (counters.containsKey(name)) {
            this.logger.info("[Failsafe] - Resetting counter ${name}")
            counters[name] = 0
        } else {
            // No counter exists
        }
    }
}