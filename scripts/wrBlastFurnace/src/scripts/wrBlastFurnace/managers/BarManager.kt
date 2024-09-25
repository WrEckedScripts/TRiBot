package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import scripts.utils.Logger

//todo rename to dispenserManager
class BarManager(val logger: Logger) {
    val BLAST_FURNACE_BRONZE_BAR = 941
    val BLAST_FURNACE_IRON_BAR = 942
    val BLAST_FURNACE_STEEL_BAR = 943
    val BLAST_FURNACE_MITHRIL_BAR = 944
    val BLAST_FURNACE_ADAMANTITE_BAR = 945
    val BLAST_FURNACE_RUNITE_BAR = 946
    val BLAST_FURNACE_SILVER_BAR = 948
    val BLAST_FURNACE_GOLD_BAR = 947

    val barBits = listOf(
        BLAST_FURNACE_BRONZE_BAR,
        BLAST_FURNACE_IRON_BAR,
        BLAST_FURNACE_STEEL_BAR,
        BLAST_FURNACE_MITHRIL_BAR,
        BLAST_FURNACE_ADAMANTITE_BAR,
        BLAST_FURNACE_RUNITE_BAR,
        BLAST_FURNACE_SILVER_BAR,
        BLAST_FURNACE_GOLD_BAR,
    )

    /**
     * Reads out the current bar dispenser state, if it holds bars, the value represent the amount of bars.
     */
    fun dispenserHoldsBars(): Boolean {
        return barBits.any { GameState.getVarbit(it) > 0 }
    }
}