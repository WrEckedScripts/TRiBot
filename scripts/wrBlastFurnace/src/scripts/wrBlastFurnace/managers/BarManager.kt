package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import scripts.utils.Logger

class BarManager(val logger: Logger) {
    /**
     * Reads out the current bar dispenser state, if it holds bars, the value represent the amount of bars.
     */
    fun dispenserHoldsBars(): Boolean {
        return GameState.getVarbit(941) == 0
    }
}