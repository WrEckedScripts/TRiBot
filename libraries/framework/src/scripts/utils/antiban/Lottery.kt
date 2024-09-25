package scripts.utils.antiban

import scripts.utils.Logger
import kotlin.random.Random

/**
 * Utility class to randomize certain actions on a probability(%) basis.
 */
object Lottery {
    var logger: Logger? = null

    fun initLogger(log: Logger) {
        this.logger = log
    }

    /**
     * Pass through a probability and lambda to execute, if our case is within the probability threshold
     */
    fun execute(probability: Double, action: () -> Unit) {
        val won: Boolean = shouldExecute(probability)

        if (won) {
            this.logger?.info("[Lottery/Antiban] - Executing randomized action")
            action()
        }
    }

    /**
     * Probability refers to the rough percentage % this action will be executed on
     */
    private fun shouldExecute(probability: Double): Boolean {
        require(probability in 0.0..1.0) { "Probability must be between 0.0 and 1.0" }

        val diceRoll = Random.nextDouble() // Gives a random value between 0.0 and 1.0
        return diceRoll < probability
    }


}