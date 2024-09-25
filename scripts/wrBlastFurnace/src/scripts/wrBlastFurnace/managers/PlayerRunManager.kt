package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Options
import org.tribot.script.sdk.antiban.AntibanProperties
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class PlayerRunManager(val logger: Logger) {
    var enableAt: Int? = null

    fun getCurrentRunEnery(): Int {
        return MyPlayer.getRunEnergy()
    }

    fun setNextRunEnablingThreshold(): Unit {
        //todo extract to AntiBan like manager?
        if (this.enableAt != null) {
            return
        }

        val antiBanValues = AntibanProperties.getPropsForCurrentChar()
        this.enableAt = TribotRandom.normal(antiBanValues.runEnergyMean, antiBanValues.runEnergyStd)
        logger.error("[PlayerRun] - next enabling at ${this.enableAt}")
    }

    fun shouldHaveRunEnabled(): Boolean {
        return this.enableAt != null
                && this.enableAt!! >= this.getCurrentRunEnery()
                || this.getCurrentRunEnery() == 100
    }

    fun enableRun(): Boolean {
        val beforeRunEnable = this.enableAt
        this.setNextRunEnablingThreshold()
        logger.debug("[PlayerRun] - Enabled run at ${beforeRunEnable}, next re-enabling at ${this.enableAt}")

        this.enableAt = null
        return Options.setRunEnabled(true)
    }
}