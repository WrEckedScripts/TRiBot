package scripts.wrBlastFurnace.behaviours.prevalidation

import org.tribot.script.sdk.Quest
import org.tribot.script.sdk.Skill
import scripts.utils.Logger

fun playerMissesRequirement(logger: Logger): Boolean {
    val requirements = mapOf(
        Skill.CRAFTING to 12,
        Skill.FIREMAKING to 16,
        Skill.MAGIC to 33,
        Skill.THIEVING to 14
    )

    var hasMissingRequirement = false;
    for ((skill, level) in requirements) {
        if (!hasMissingRequirement) {
            hasMissingRequirement = missesRequirementFor(skill, level, logger)
        }
    }

    if (!hasMissingRequirement) {
        hasMissingRequirement = Quest.THE_GIANT_DWARF.state == Quest.State.NOT_STARTED
        logger.info("Player QuestState for the giant dwarf = ${Quest.THE_GIANT_DWARF.state}")
    }

    return hasMissingRequirement;
}

private fun missesRequirementFor(skill: Skill, requiredLevel: Int, logger: Logger): Boolean {
    if (skill.actualLevel < requiredLevel) {
        logger.error("BlastFurnace requires '${skill.name}' level '${requiredLevel}' player has '${skill.actualLevel}'")
        return true
    }

    logger.info("Player complies with requirement for ${skill.name} ${skill.actualLevel} / ${requiredLevel}")
    return false
}