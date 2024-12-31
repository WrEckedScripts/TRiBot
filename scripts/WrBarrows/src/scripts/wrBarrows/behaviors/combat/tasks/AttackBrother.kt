package scripts.wrBarrows.behaviors.combat.tasks

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.NpcQuery
import org.tribot.script.sdk.query.Query
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container
import kotlin.jvm.optionals.getOrNull

class AttackBrother(val managers: Container) {

    //TODO track actual killing (markCrypt as complete etc)
    // Looking at our earlier tryout with a CombatManager, this all is still much incomplete

    fun should(): Boolean {
        val brotherIsAttackingMe = this.getBrotherQuery().isInteractingWithMe.equals(true)
        val iAmAttacking = this.getBrotherQuery().isMyPlayerInteractingWith.equals(true)

        return !brotherIsAttackingMe || !iAmAttacking
    }

    fun execute(): Boolean {
        if (!this.should()) {
            return true
        }

        val target = this.getBrotherQuery().findBestInteractable().get()

        target.interact("Attack")

        Waiting.wait(FatigueResolver.getMilliseconds() * 2)

        return MyPlayer.get()
            .get()
            .interactingCharacter
            .getOrNull()
            ?.let { interacting -> interacting == target }
            ?: false
    }

    private fun getBrotherQuery(): NpcQuery {
        val brother = this.managers.roomManager.getTargetCrypt().room.brother
        return Query.npcs()
            .nameEquals(brother.brotherName)
            .filter { it.isValid }
            .hasOverheadIcon()
    }
}