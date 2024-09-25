package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Options
import org.tribot.script.sdk.antiban.AntibanProperties
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class PlayerRunManager(val logger: Logger) {
    private var enableAt: Int? = null

    private fun getCurrentRunEnergy(): Int {
        return MyPlayer.getRunEnergy()
    }

    private fun getIsRunning(): Boolean {
        return Options.isRunEnabled()
    }

    fun getNextEnableAtValue(): Int? {
        return this.enableAt
    }

    fun setNextRunEnablingThreshold() {
        val antiBanValues = AntibanProperties.getPropsForCurrentChar()
        this.enableAt = TribotRandom.normal(antiBanValues.runEnergyMean, antiBanValues.runEnergyStd)

        logger.error("[PlayerRun] - We're going to enable running once your player's stamina reaches +/- ${this.enableAt}")
    }

    /**
     * Conditional for ensuring we're running
     *
     * - TRUE if our player is running or is below our next enabling threshold
     * - returns FALSE if we should be running.
     */
    fun satisfiesRunExpectation(): Boolean {
        if (this.getIsRunning()) {
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
        val beforeRunEnable = this.getCurrentRunEnergy()
        this.setNextRunEnablingThreshold()
        logger.info("[PlayerRun] - We've enabled running at ${beforeRunEnable}, next re-enabling will happen once your stamina reaches +/- ${this.enableAt}")

        return Options.setRunEnabled(true)
    }
}