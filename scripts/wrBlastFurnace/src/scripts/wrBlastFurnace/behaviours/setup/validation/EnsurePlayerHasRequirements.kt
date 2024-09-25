package scripts.wrBlastFurnace.behaviours.setup.validation

import org.tribot.script.sdk.Quest
import org.tribot.script.sdk.Skill
import scripts.utils.Logger

class EnsurePlayerHasRequirements(val logger: Logger) {
    var hasMissingRequirement: Boolean = false;

    fun playerMissesRequirement(): Boolean {
        val requirements = mapOf(
            Skill.CRAFTING to 12,
            Skill.FIREMAKING to 16,
            Skill.MAGIC to 33,
            Skill.THIEVING to 14
        )

        for ((skill, level) in requirements) {
            if (!this.hasMissingRequirement) {
                this.hasMissingRequirement = this.missesRequirementFor(skill, level)
            }
        }

        if (!this.hasMissingRequirement) {
            this.hasMissingRequirement = Quest.THE_GIANT_DWARF.state == Quest.State.NOT_STARTED
            logger.info("The giant dwarf = ${Quest.THE_GIANT_DWARF.state}")
        }

        return this.hasMissingRequirement;
    }

    private fun missesRequirementFor(skill: Skill, requiredLevel: Int): Boolean {
        if (skill.actualLevel < requiredLevel) {
            logger.error("BlastFurnace requires '${skill.name}' level '${requiredLevel}' player has '${skill.actualLevel}'")
            return true
        }

        logger.info("Player complies with requirement for ${skill.name} ${skill.actualLevel} / ${requiredLevel}")
        return false
    }
}