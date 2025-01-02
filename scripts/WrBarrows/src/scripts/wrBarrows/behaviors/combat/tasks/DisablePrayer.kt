package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.Prayer
import org.tribot.script.sdk.Waiting

class DisablePrayer {
    fun satisfied(): Boolean {
        return Prayer.getActivePrayers().isEmpty()
    }

    fun execute(): Boolean {
        if (this.satisfied()) {
            return true
        }

        Waiting.waitUntil(3_000, 350) {
            Prayer.getActivePrayers().forEach { it.disable() }

            this.satisfied()
        }

        return this.satisfied()
    }
}