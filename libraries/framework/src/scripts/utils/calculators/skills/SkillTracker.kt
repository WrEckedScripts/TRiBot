package scripts.utils.calculators.skills

import org.tribot.script.sdk.Skill
import scripts.utils.calculators.CachedPerHourCalculator
import scripts.utils.formatters.CompactNotator
import scripts.utils.formatters.TimeNotator

data class SkillTracker(
    val skill: Skill,
    val startingExp: Int,
    val startingLevel: Int
) {
    private val calculator: CachedPerHourCalculator = CachedPerHourCalculator(
        System.currentTimeMillis()
    )

    private fun levels(): Int {
        return skill.actualLevel - startingLevel
    }

    private fun gained(): Int {
        return skill.xp - this.startingExp
    }

    private fun calculate(): Int {
        return this.calculator.perHour(this.startingExp, this.skill.xp)
    }

    private fun timeToNextLevel(): Long {
        val expNeeded = this.skill.currentXpToNextLevel
        val expPerHour = this.calculate()

        if (expPerHour <= 0) {
            return 0L
        }

        val hoursToNextLevel = expNeeded.toDouble() / expPerHour
        val inMilliseconds = (hoursToNextLevel * 60 * 60 * 1000).toLong()

        return inMilliseconds
    }

    fun expLabel(): String {
        val gained = CompactNotator.format(this.gained())
        val calculated = CompactNotator.format(this.calculate())

        return String.format("${gained} | (${calculated})")
    }

    fun levelLabel(): String {
        return "${this.skill.actualLevel} (${this.skill.xpPercentToNextLevel} %) | (+ ${this.levels()})"
    }

    fun timeToNextLevelLabel(): String {
        return TimeNotator.formatMillisecondsToHMS(this.timeToNextLevel())
    }
}
