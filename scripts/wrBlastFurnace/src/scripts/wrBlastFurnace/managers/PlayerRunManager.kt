package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Options
import org.tribot.script.sdk.antiban.AntibanProperties
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class PlayerRunManager(val logger: Logger) {
    var enableAt: Int = 0

    fun getCurrentRunEnery(): Int {
        return MyPlayer.getRunEnergy()
    }

    fun hasRunEnabled(): Boolean {
        return Options.isRunEnabled()
    }

    fun setNextRunEnablingThreshold(): Unit {
        //todo extract to AntiBan like manager?
        val antiBanValues = AntibanProperties.getPropsForCurrentChar()
        this.enableAt = TribotRandom.normal(antiBanValues.runEnergyMean, antiBanValues.runEnergyStd)
        logger.error("[PlayerRun] - next enabling at ${this.enableAt}")
    }

    fun shouldHaveRunEnabled(): Boolean {
        logger.debug("shouldHaveRunEnabled: ${this.enableAt >= this.getCurrentRunEnery()}")
        return this.enableAt >= this.getCurrentRunEnery()
    }

    fun enableRun(): Boolean {
        logger.debug("[PlayerRun] - ${this.enableAt} <= ${this.getCurrentRunEnery()}")

        this.setNextRunEnablingThreshold()
        return Options.setRunEnabled(true)
    }
}