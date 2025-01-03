package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.query.NpcQuery
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State
import scripts.wrBarrows.player.BarrowsBrother

class CheckIAmUnderAttack(val managers: Container) {
    fun satisfied(): Boolean {
        return false
    }

    fun execute(): Boolean {
        val spawnedBrother = BarrowsBrother.values().firstOrNull {
            this.findBrotherQuery(it.brotherName).isAny
        }

        if (null == spawnedBrother) {
            Logger("LookForActiveCombat").info("No Brother attacking me")
            return true
        }

        Logger("LookForActiveCombat").error("Fighting mode enabled")
        return this.managers.stateManager.set(State.FIGHT.name)
    }

    private fun findBrotherQuery(name: String): NpcQuery {
        return Query.npcs()
            .nameEquals(name)
            .isInteractingWithMe
    }
}