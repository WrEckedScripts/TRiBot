package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.NpcQuery
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State
import kotlin.jvm.optionals.getOrNull

class AttackBrother(val managers: Container) {

    //TODO track actual killing (markCrypt as complete etc)
    // Looking at our earlier tryout with a CombatManager, this all is still much incomplete

    fun satisfied(): Boolean {
        // Do not re-trigger when we're already fighting.
        if (!this.isInCombat()) {
            Logger("AttackBrother").warn("Not in combat")
            return false
        }

        return this.isInCombat()
    }

    fun execute(): Boolean {
        if (this.satisfied()) {
            return true
        }

        if (this.managers.roomManager.getTargetCrypt() != this.managers.roomManager.getCurrentCrypt()!!.value) {
            this.managers.stateManager.set(State.ROOM.name)

            return true
        }

        // Wait until brother is spawned
        Waiting.waitUntil(3_000, 750) {
            this.getBrotherQuery().isAny
        }

        val target = this.getBrotherQuery().findBestInteractable().getOrNull()

        if (null == target) {
            Logger("AttackBrother").debug("No target found...?")
            return true
        }

        Logger("AttackBrother").debug("execute() Target: ${target}")

        target.interact("Attack")

        Logger("AttackBrother").debug("execute() Are we attacking??")
        Waiting.wait(FatigueResolver.getMilliseconds() * 2)


        val isFighting = Waiting.waitUntil(3_000, 750) {
            this.isInCombat()
        }

        managers.stateManager.set(State.FIGHT.name)

        Logger("AttackBrother").debug("execute() isFighting ${isFighting}")

        // Return true only when not fighting anymore to indicate we're managed to kill.
        return isFighting
    }

    fun isInCombat(): Boolean {
        val target = this.getBrotherQuery().findBestInteractable().getOrNull()

        if (null == target) {
            Logger("isInCombat()").warn("No valid target..")
            return false
        }

        val myPlayerIsAttacking = MyPlayer.get()
            .get()
            .interactingCharacter
            .getOrNull()
            ?.let { interacting ->
                interacting == target
            }
            ?: false

        return myPlayerIsAttacking
    }

    private fun getBrotherQuery(): NpcQuery {
        //TODO what if we're within the tunnels?
        val brother = this.managers.roomManager.getCurrentCrypt()!!.value.room.brother
        Logger("BrotherQuery").error("NPCName: ${brother.brotherName}")

        val npc = Query.npcs()
            .nameEquals(brother.brotherName)
            .filter { it.isValid && (!it.isInteracting || it.isInteractingWithMe) }

        return npc
    }
}