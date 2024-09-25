package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.input.Keyboard
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

class StaminaManager(val logger: Logger, val playerRunManager: PlayerRunManager) {
    private fun isActive(): Boolean {
        return MyPlayer.isStaminaActive()
    }

    fun notOutOfPotions(): Boolean {
        return Query.bank()
            .nameContains("Stamina potion")
            .count() == 0
    }

    fun sipStamina(): Boolean{
        logger.info("Sipping...")
        Query.inventory()
            .nameContains("Stamina potion")
            .findRandom()
            .map {
                it.click("Drink")
                Waiting.waitUntil { this.isActive() }

                // Antiban / antiprofile:
                // - Full inv deposit
                // - deposit specific potion
                it.click()
                Waiting.waitNormal(500, 15)
            }

        return this.isActive()
    }

    fun satisfiesStaminaState(): Boolean {
        // If we're rand. X below next re-enable (PlayerRunManager)
        if (this.isActive()) {
            return true
        }

        if (this.playerRunManager.isRunning()) {
            return true
        }

        return false
    }
}
