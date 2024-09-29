package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import scripts.utils.Logger

class MeltingPotManager(val logger: Logger) {
    private val MELTING_POT_COAL = 949

    private val meltBits = listOf(
        MELTING_POT_COAL
    )

    /**
     * Helper method, that checks the melting pot,
     * and determines if we got more than the passed threshold in coal, within the melting pot
     *
     * Use case: To skip the coal filling stage, whenever we got plenty of coal stored.
     * - This allows users to always safely run their bot, nonetheless earlier Blast Furnace work.
     */
    fun containsCoalMoreThan(threshold: Int): Boolean {
        return GameState.getVarbit(this.MELTING_POT_COAL) > threshold
    }

    fun getCoalCount(): Int {
        return GameState.getVarbit(this.MELTING_POT_COAL)
    }
}