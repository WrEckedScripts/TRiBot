package scripts.wrCrafting

import org.tribot.script.sdk.Log
import org.tribot.script.sdk.Skill
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

interface ProgressManagerInterface {
    val trackingSkill: Skill
    val startingExperience: Int

    fun logger(): Logger
}

class ProgressManager(
    override val trackingSkill: Skill,
    override val startingExperience: Int,

    ) : ProgressManagerInterface {
    private val startedAt = System.currentTimeMillis()
    private var craftedItems: Int = 0

    override fun logger(): Logger {
        return Logger("Progress")
    }

    fun incrementCraftedItems(increment: Int) {
        logger().debug("incrementing crafted items from: ${this.craftedItems} with: ${increment}")
        this.craftedItems += increment
        logger().debug("incrementing done, new count: ${this.craftedItems}")
    }

    fun getCraftedItemsCount(): String {
        return formatNumber(this.craftedItems.toDouble())
    }

    fun experienceGained(): Int {
        return trackingSkill.xp - startingExperience
    }

    fun experiencePerHour(): Double {
        val currentAt = System.currentTimeMillis()
        val experienceGained = experienceGained()
        val elapsedTimeMillis = currentAt - startedAt
        val elapsedTimeHours = elapsedTimeMillis / 3600000.0

        if (elapsedTimeMillis < 1) {
            return 0.0
        }

        val experiencePerHour = experienceGained / elapsedTimeHours

        return experiencePerHour
    }

    private fun formatNumber(value: Double): String {
        return when {
            value >= 1_000_000 -> String.format("%.2fM", value / 1_000_000)
            value >= 1_000 -> String.format("%.2fK", value / 1_000)
            else -> value.toString()
        }
    }

    fun experiencePaintValue(): String {
        val experienceGained = experienceGained()
        val experiencePerHour = experiencePerHour()
        val formattedExperienceGained = formatNumber(experienceGained.toDouble())
        val formattedExperiencePerHour = formatNumber(experiencePerHour)
        return "$formattedExperienceGained / (${formattedExperiencePerHour})"
    }


}