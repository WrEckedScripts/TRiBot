package scripts.utils.calculators.skills

import org.tribot.script.sdk.Skill

object TrackerCollection {
    private var skills: MutableMap<String, SkillTracker> = mutableMapOf()

    fun add(skill: Skill) {
        this.skills[skill.name] = SkillTracker(skill, skill.xp, skill.actualLevel)
    }

    fun get(name: String): SkillTracker? {
        return this.skills.get(name)
    }
}