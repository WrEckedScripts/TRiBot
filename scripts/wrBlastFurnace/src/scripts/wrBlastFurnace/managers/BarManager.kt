package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import scripts.utils.Logger

class DispenserManager(val logger: Logger) {
    private val BLAST_FURNACE_BRONZE_BAR = 941
    private val BLAST_FURNACE_IRON_BAR = 942
    private val BLAST_FURNACE_STEEL_BAR = 943
    private val BLAST_FURNACE_MITHRIL_BAR = 944
    private val BLAST_FURNACE_ADAMANTITE_BAR = 945
    private val BLAST_FURNACE_RUNITE_BAR = 946
    private val BLAST_FURNACE_GOLD_BAR = 947
    private val BLAST_FURNACE_SILVER_BAR = 948

    private val barBits = listOf(
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
     * Reads out the current bar dispenser state,
     * if it holds bars, the value of the varbit represent the amount of bars.
     */
    fun holdsBars(): Boolean {
        return barBits.any { GameState.getVarbit(it) > 0 }
    }
}