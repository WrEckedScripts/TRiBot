package scripts.wrBlastFurnace.managers

import org.tribot.api.input.Mouse
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class StaminaManager(val logger: Logger, val playerRunManager: PlayerRunManager) {
    var minimumStaminaLevel: Int = TribotRandom.normal(15, 5)

    private fun isActive(): Boolean {
        return MyPlayer.isStaminaActive()
    }

    fun notOutOfPotions(): Boolean {
        return Query.bank()
            .nameContains("Stamina potion")
            .count() == 0
    }

    fun sipStamina(): Boolean {
        val currentMouseSpeed = Mouse.getSpeed()
        logger.info("[Antiban] - Temporarily increasing Mouse Speed")
        Mouse.setSpeed(currentMouseSpeed + TribotRandom.normal(95, 19))

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
                    Waiting.waitNormal(120, 25)
                    clicked
                }
            }

        Mouse.setSpeed(currentMouseSpeed)
        this.minimumStaminaLevel = TribotRandom.normal(17, 4)

        return this.isActive()
    }

    fun satisfiesStaminaState(): Boolean {
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
