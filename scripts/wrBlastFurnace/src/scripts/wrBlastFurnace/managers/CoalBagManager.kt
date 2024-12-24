package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.MessageListening
import scripts.utils.Logger

class CoalBagManager(val logger: Logger) {
    private var isEmpty = true

    /**
     * Gets the boolean representation of the current state
     * - false = filled
     * - true = empty
     */
    fun getState(): Boolean {
        return this.isEmpty
    }

    fun getStateAsString(): String {
        return when (this.isEmpty) {
            false -> "filled"
            true -> "empty"
        }
    }

    fun setState(empty: Boolean) {
        logger.warn("[CoalBagManager] Set State to empty:$empty")
        this.isEmpty = empty
        logger.info("[CoalBagManager] isEmpty:${this.isEmpty}")
    }

    //The coal bag is empty. / The coal bag is now empty.
    // The coal bag contains 27 pieces of coal.
    fun registerListeners() {
        logger.warn("[CoalBagManager] Registering Listeners..")

        MessageListening.addServerMessageListener { message: String ->
            when (message) {
                "The coal bag is empty." -> this.setState(true)
                "The coal bag is now empty." -> this.setState(true)
                "The coal bag contains 27 pieces of coal." -> this.setState(false)
            }
        }

        logger.warn("[CoalBagManager] Done Registering Listeners")
    }
}