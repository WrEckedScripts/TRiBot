package scripts.wrMotherlode.managers

import org.tribot.script.sdk.GameState
import scripts.utils.Logger

class SackManager(val logger: Logger) {
    private val maximumSackSpace = 108
    private val sackVarbit = 5558

    fun needsFilling(): Boolean {
        return GameState.getVarbit(this.sackVarbit) < this.maximumSackSpace
    }

    fun getRemainingSpace(): Int {
        return this.maximumSackSpace - GameState.getVarbit(this.sackVarbit)
    }
}