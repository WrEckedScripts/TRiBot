package scripts.wrSulphurNagua.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.NpcQuery
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Npc
import scripts.utils.Logger
import kotlin.jvm.optionals.getOrNull

class CombatManager {
    private var target: Npc? = null

    fun isKilled(): Boolean {
        if (this.target === null) {
            return true
        }

        return !this.target!!.isValid
    }

    fun satisfiesAttack(): Boolean {
        if (this.target === null || this.target?.isValid == false || this.target?.healthBarPercent == 0.0) {
            this.target = null

            return true
        }

        val snapshotPlayerAnimation = MyPlayer.getAnimation()
        if (this.target?.interact("Attack") == false) {
            this.target = null
            return true
        }

        val isInCombat = Waiting.waitUntil(2_500) { !this.satisfiesInCombatState() }
        val isAttacking = Waiting.waitUntil(2_500) {
            this.target?.hitsplats.isNullOrEmpty()
                    && MyPlayer.getAnimation() != -1
                    && MyPlayer.getAnimation() != snapshotPlayerAnimation
        }

        return !(isInCombat && isAttacking)
    }

    fun satisfiesInCombatState(): Boolean {
        if (this.target === null || this.target?.isValid == false) {
            return true
        }

        // Check if the player is interacting with the target npc
        val isPlayerTargetingNpc = this.isAttackingTarget()

        // Check if the target npc is interacting with the player
        val isNpcTargetingPlayer = this.isBeingAttackedByTarget()

        Logger("InCombat").warn("isPlayerTargetingNpc ${!isPlayerTargetingNpc}")
        Logger("InCombat").warn("isNpcTargetingPlayer ${!isNpcTargetingPlayer}")
        Logger("satisfiesInCombatState").error("Results in: ${!isPlayerTargetingNpc && !isNpcTargetingPlayer}")
        return !isPlayerTargetingNpc && !isNpcTargetingPlayer
    }

    fun isBeingAttackedByTarget(): Boolean {
        Logger("IsBeingAttackedByTarget").warn("Target: ${this.target?.interactingCharacter?.getOrNull()}")
        Logger("IsBeingAttackedByTarget").warn("MyPlayer: ${MyPlayer.get().getOrNull()}")
        return this.target?.interactingCharacter?.getOrNull() == MyPlayer.get().getOrNull()
    }

    fun isAttackingTarget(): Boolean {
        return MyPlayer.get().getOrNull()
            ?.interactingCharacter
            ?.getOrNull()
            ?.let { interacting ->
                interacting == this.target
            } ?: false
    }

    fun getNpcQuery(): NpcQuery {
        return Query.npcs()
            .nameEquals("Sulphur Nagua")
            .filter { it.isValid && it.isInteracting == false }
            .isReachable
    }
}