package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.Prayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.util.TribotRandom
import scripts.wrBarrows.player.BarrowsBrother

class EnablePrayer {
    fun satisfied(brother: BarrowsBrother): Boolean {
        return brother.prayer == null
    }

    fun execute(brother: BarrowsBrother): Boolean {
        if (this.satisfied(brother)) {
            return true
        }

        val prayer = brother.prayer!!.protectionPrayer
        if (Prayer.getPrayerPoints() < TribotRandom.uniform(7, 19)) {
            this.potUp()
        }

        Waiting.waitUntil(15_000) {
            prayer.enable()
        }

        // Slight "tick" wait, until we query enabled state.
        Waiting.wait(700)

        return prayer.isEnabled()
    }

    private fun potUp() {
        ConsumePotion("Prayer potion").execute()
    }
}