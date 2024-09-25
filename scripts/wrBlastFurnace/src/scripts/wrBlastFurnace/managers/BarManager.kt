package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import scripts.utils.Logger

class BarManager(val logger: Logger) {
    fun inventoryHoldsOres(): Boolean {
        // After bank close, this is re-evaluated and too fast, which rebanks the ores
        // within the SmeltsBarsNode, we might need to slow down returning the withdraw status inside the condition
        // not sure if that slows down all banking actions...

        Waiting.waitNormal(900, 60)
        val hasCopper = Inventory.contains("Copper ore")
        val hasTin = Inventory.contains("Tin ore")
        val holdingOres = hasCopper && hasTin

        return holdingOres
    }

    /**
     * Reads out the current bar dispenser state, if it holds bars, the value represent the amount of bars.
     */
    fun dispenserHoldsBars(): Boolean {
        return GameState.getVarbit(941) == 0
    }
}