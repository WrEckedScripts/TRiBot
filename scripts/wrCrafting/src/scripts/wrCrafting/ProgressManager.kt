package scripts.wrCrafting

import org.tribot.script.sdk.Log
import org.tribot.script.sdk.Skill
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query

interface ProgressManagerInterface {
    val trackingSkill: Skill
    val startingExperience: Int
}

class ProgressManager(
    override val trackingSkill: Skill,
    override val startingExperience: Int,

    ) : ProgressManagerInterface {
    private val startedAt = System.currentTimeMillis()
    private var craftedItems: Int = 0

    fun incrementCraftedItems(increment: Int) {
        this.craftedItems += increment
    }

    fun getCraftedItemsCount(): String {
        return formatNumber(this.craftedItems.toDouble())
    }

    fun experienceGained(): Int {
        return trackingSkill.xp - startingExperience
    }

    fun experiencePerHour(): Double {
        Log.debug("CALLED-EXPHR")
        val currentAt = System.currentTimeMillis()
        val experienceGained = experienceGained()
        val elapsedTimeMillis = currentAt - startedAt
        val elapsedTimeHours = elapsedTimeMillis / 3600000.0

        if (elapsedTimeMillis < 1) {
            return 0.0
        }

        val experiencePerHour = experienceGained / elapsedTimeHours
        Log.debug("Estimated exp/hr ${experiencePerHour}")

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