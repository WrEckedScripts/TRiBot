package scripts.wrMotherlode.managers

import org.tribot.script.sdk.GameState
import scripts.utils.Logger

class SackManager(val logger: Logger) {
    private val maximumSackSpace = 189 // todo calculate if sack upgraded or not
    private val sackVarbit = 5558

    /**
     * - TRUE | When the sack holds less than the maximum space, we know it's not full
     * - FALSE | When the sack holds more or equal the max space.
     */
    fun canBeFilled(): Boolean {
        val sackHolds = GameState.getVarbit(this.sackVarbit)
        return sackHolds < this.maximumSackSpace
    }

    fun isEmpty(): Boolean {
        return GameState.getVarbit(this.sackVarbit) == 0
    }

    fun getRemainingSpace(): Int {
        return this.maximumSackSpace - GameState.getVarbit(this.sackVarbit)
    }

    // max space: 189
    // current: 25
    // sack: 164
    // if current != 0, we should re-loot
    // only if, the sack reaches the max space, we should go into collecting.
}