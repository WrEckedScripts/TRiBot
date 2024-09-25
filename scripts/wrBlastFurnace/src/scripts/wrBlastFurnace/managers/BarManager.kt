package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import scripts.utils.Logger

class BarManager(val logger: Logger) {
    fun inventoryHoldsOres(): Boolean {
        // After bank close, this is re-evaluated and too fast, which rebanks the ores
        // within the SmelsBarsNode, we might need to slow down returning the withdraw status inside the condition
        // not sure if that slows down all banking actions...
        Waiting.waitNormal(800, 60)
        val hasCopper = Inventory.contains("Copper ore")
        val hasTin = Inventory.contains("Tin ore")
        val holdingOres = hasCopper && hasTin
        logger.debug("[BARMANAGER] - copper: ${hasCopper} | tin: ${hasTin}")

        return holdingOres
    }

    //State for between loading ores and withdrawing bars
    fun hasCollected(): Boolean {
        return GameState.getVarbit(941) == 0
    }
}