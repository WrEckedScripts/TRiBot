package scripts.wrBarrows.managers

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.NpcQuery
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Npc
import scripts.utils.Logger
import scripts.wrBarrows.player.BarrowsBrother
import kotlin.jvm.optionals.getOrNull

class CombatManager(val logger: Logger) {
    private var target: Npc? = null
    private var remainingBrothers = BarrowsBrother.values().filter { it.varbit.get() == 0 }.map { it.brotherName }

    fun targetBrotherIsKilled(): Boolean {
        Logger("TargetKilled").warn("Target = ${this.target?.name} & ${this.target?.isValid}")
        if (this.target === null) {
            return true
        }

        // get varbit related to brother
        val killed = BarrowsBrother.values().filter {
            it.brotherName == this.target!!.name
        }.firstOrNull()?.varbit?.get() == 1 && !this.target!!.isValid

        Logger("TargetKilled").error("Killed state: ${killed}")
        Logger("TargetKilled").error("Invalid state: ${!this.target!!.isValid}")

        return killed
    }

    // if a brother is spawned we should attack and set it as target
    fun targetBrotherIsSpawned(): Boolean {
        val query = this.getNpcQuery()
        val spawned = query.isAny

        if (spawned) {
            this.target = query.findBestInteractable().getOrNull()
        }

        return spawned
    }

    fun getTargetBrother(): BarrowsBrother? {
        if (this.target === null) {
            return null
        }

        val targetBrother = BarrowsBrother.values().filter {
            it.brotherName == this.target!!.name
        }.firstOrNull()

        return targetBrother
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
        val isPlayerTargetingNpc = this.playerIsAttacking()

        // Check if the target npc is interacting with the player
        val isNpcTargetingPlayer = this.brotherIsAttacking()

        Logger("InCombat").warn("isPlayerTargetingNpc ${!isPlayerTargetingNpc}")
        Logger("InCombat").warn("isNpcTargetingPlayer ${!isNpcTargetingPlayer}")

        return !isPlayerTargetingNpc && !isNpcTargetingPlayer
    }

    private fun brotherIsAttacking(): Boolean {
        return this.target!!.interactingCharacter.getOrNull() == MyPlayer.get().getOrNull()
    }

    fun playerIsAttacking(): Boolean {
        return MyPlayer.get().getOrNull()
            ?.interactingCharacter
            ?.getOrNull()
            ?.let { interacting ->
                interacting == this.target
            } ?: false
    }

    private fun getNpcQuery(): NpcQuery {
        return Query.npcs()
            .nameEquals(*this.remainingBrothers.toTypedArray())
            .filter { it.isValid && it.isInteractingWithMe }
            .isReachable
    }
}