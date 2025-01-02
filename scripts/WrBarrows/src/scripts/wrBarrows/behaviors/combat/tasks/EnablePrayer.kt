package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.Prayer
import org.tribot.script.sdk.Waiting

class EnablePrayer(val prayer: Prayer) {

    fun should(): Boolean {
        // TODO only on certain brothers
        return true
    }

    fun execute(): Boolean {
        // Pot up
        if (Prayer.getPrayerPoints() < 30) {
            this.potUp()
        }

        Waiting.waitUntil(15_000) {
            this.prayer.enable()
        }

        // Slight "tick" wait, until we query enabled state.
        Waiting.wait(700)

        return this.prayer.isEnabled()
    }

    private fun potUp() {
        ConsumePotion("Prayer potion").execute()
    }
}