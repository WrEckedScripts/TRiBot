package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Options
import org.tribot.script.sdk.antiban.AntibanProperties
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class PlayerRunManager(val logger: Logger) {
    private var enableAt: Int? = null

    fun getCurrentRunEnergy(): Int {
        return MyPlayer.getRunEnergy()
    }

    fun isRunning(): Boolean {
        return Options.isRunEnabled()
    }

    fun setNextRunEnablingThreshold() {
        val antiBanValues = AntibanProperties.getPropsForCurrentChar()
        this.enableAt = TribotRandom.normal(antiBanValues.runEnergyMean, antiBanValues.runEnergyStd)
    }

    /**
     * Conditional for ensuring we're running
     *
     * - TRUE if our player is running or is below our next enabling threshold
     * - returns FALSE if we should be running.
     */
    fun satisfiesRunExpectation(): Boolean {
        if (this.isRunning()) {
            return true
        }

        if (this.enableAt == null) {
            return false
        }

        if (this.getCurrentRunEnergy() == 100) {
            return false;
        }

        if (this.enableAt!! <= this.getCurrentRunEnergy()) {
            return false
        }

        return true
    }

    fun enableRun(): Boolean {
        this.setNextRunEnablingThreshold()

        return Options.setRunEnabled(true)
    }
}