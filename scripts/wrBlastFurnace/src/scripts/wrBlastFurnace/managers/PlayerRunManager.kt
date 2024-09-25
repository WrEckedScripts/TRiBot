package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Options
import org.tribot.script.sdk.antiban.AntibanProperties
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class PlayerRunManager(val logger: Logger) {
    var enableAt: Int? = null

    fun getCurrentRunEnergy(): Int {
        return MyPlayer.getRunEnergy()
    }

    fun setNextRunEnablingThreshold() {
        val antiBanValues = AntibanProperties.getPropsForCurrentChar()
        this.enableAt = TribotRandom.normal(antiBanValues.runEnergyMean, antiBanValues.runEnergyStd)

        logger.error("[PlayerRun] - next enabling at ${this.enableAt}")
    }

    fun shouldHaveRunEnabled(): Boolean {
        if (this.enableAt == null) {
            return false
        }

        return this.getCurrentRunEnergy() == 100
                || this.enableAt!! >= this.getCurrentRunEnergy()
    }

    fun enableRun(): Boolean {
        val beforeRunEnable = this.enableAt
        this.setNextRunEnablingThreshold()
        logger.info("[PlayerRun] - Enabled run at ${beforeRunEnable}, next re-enabling at ${this.enableAt}")

        return Options.setRunEnabled(true)
    }
}