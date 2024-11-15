package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger
import scripts.wrBlastFurnace.gui.Settings

class StaminaManager(val logger: Logger, val playerRunManager: PlayerRunManager) {
    private var minimumStaminaLevel: Int = TribotRandom.normal(15, 5)

    private fun shouldSip(): Boolean {
        return Settings.staminaChecked
    }

    private fun isActive(): Boolean {
        return MyPlayer.isStaminaActive()
    }

    fun notOutOfPotions(): Boolean {
        return Query.bank()
            .nameContains("Stamina potion")
            .count() == 0
    }

    fun sipStamina(): Boolean {
        Query.inventory()
            .nameContains("Stamina potion")
            .findRandom()
            .map {
                it.click("Drink")
                Waiting.waitUntil { this.isActive() }

                // Antiban / antiprofile:
                // - Full inv deposit
                // - deposit specific potion
                Waiting.waitUntil {
                    val clicked = it.click()
                    Waiting.waitNormal(240, 25)
                    clicked
                }
            }

        this.minimumStaminaLevel = TribotRandom.normal(17, 4)

        return this.isActive()
    }

    fun satisfiesStaminaState(): Boolean {
        if (!this.shouldSip()) {
            return true
        }

        if (this.isActive()) {
            return true
        }

        if (
            this.playerRunManager.isRunning()
            && this.playerRunManager.getCurrentRunEnergy() > this.minimumStaminaLevel
        ) {
            return true
        }

        return false
    }
}
