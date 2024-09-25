package scripts.wrCombat

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.antiban.Antiban
import org.tribot.script.sdk.query.NpcQuery
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.Npc
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

interface ICombatManager {
    val logger: Logger
    val combatArea: Area
    val npcNames: List<String>
    val npcQuery: NpcQuery
    val attackOption: String
    var targetNpc: Npc?
    var killCount: Int

    fun attackTargetNpc(): Boolean

    fun setNewTargetNpc()

    fun resetTargetNpc()

    fun resetInventory()
}

fun ICombatManager.incKillCount() {
    killCount++
}

fun ICombatManager.isTargetNull() = targetNpc === null

fun ICombatManager.isTargetValid() = targetNpc?.isValid == true

fun ICombatManager.isTargetNotValid() = targetNpc?.isValid == false

fun ICombatManager.isTargetNotNull() = targetNpc !== null

fun ICombatManager.inCombatArea() = combatArea.containsMyPlayer()

fun ICombatManager.inCombat(): Boolean {
    if (isTargetNull() || isTargetNotValid()) return false

    // Check if the player is interacting with the target npc
    val isPlayerTargetingNpc = MyPlayer.get().getOrNull()
        ?.interactingCharacter
        ?.getOrNull()
        ?.let { interacting ->
            interacting == targetNpc
        } ?: false

    // Check if the target npc is interacting with the player
    val isNpcTargetingPlayer = targetNpc!!.interactingCharacter.getOrNull() == MyPlayer.get().getOrNull()
    return isPlayerTargetingNpc && isNpcTargetingPlayer
}

fun ICombatManager.getInteractingWithMe() = npcQuery.isInteractingWithMe.toList()

fun ICombatManager.getInteractingCharacter() = MyPlayer.get()
    .flatMap { it.interactingCharacter }
    .getOrNull()

fun ICombatManager.getClosestNonInteracting() = npcQuery.isNotInteractingWithCharacter
    .isNotBeingInteractedWith
    .findClosest()
    .getOrNull()

class CombatManager(
    override val combatArea: Area,
    override val npcNames: List<String>,
) : ICombatManager {
    override val logger: Logger = Logger("Combat Manager")
    override var targetNpc: Npc? = null
    override val attackOption: String = "Attack"
    override var killCount: Int = 0
    override val npcQuery
        get() = Query.npcs()
            .nameContains(*npcNames.toTypedArray())
            .filter { it.isValid }
            .filter { combatArea.contains(it) }
            .isReachable

    override fun resetTargetNpc() {
        logger.debug("Reset target npc")
        targetNpc = null
    }

    override fun setNewTargetNpc() {
        logger.debug("Setting new target npc")

        // Prioritize the npc interacting with our player
        val interactingNpc = getInteractingCharacter()
        if (interactingNpc is Npc) {
            if (npcNames.contains(interactingNpc.name)) {
                targetNpc = interactingNpc
                return
            }
        }

        // Prioritize npcs that are directly interacting with the player
        val interactingWithMe = getInteractingWithMe()

        // Filter by npc ids or names to ensure targeting the correct npcs.
        val filteredInteractingWithMe = interactingWithMe.filter {
            npcNames.contains(it.name)
        }

        // If there's a npc directly interacting with the player that matches our targets, prioritize it
        if (filteredInteractingWithMe.isNotEmpty()) {
            targetNpc = filteredInteractingWithMe.first()
            return
        }

        // If no directly interacting npcs match, find the closest npc that matches the criteria
        val closestMatchingNpc = getClosestNonInteracting()

        targetNpc = if (closestMatchingNpc !== null) {
            closestMatchingNpc
        } else {
            null
        }

        logger.debug("Found a target!")
    }

    override fun attackTargetNpc(): Boolean {
        logger.debug("Attacking target npc 123")

        if (isTargetNull() || isTargetNotValid() || targetNpc?.healthBarPercent == 0.0) {
            logger.debug("Test in if")
            return false
        }

        val animationBeforeAttacking = MyPlayer.getAnimation()

        logger.debug("BEFORE INTERACT")
        if (targetNpc?.interact(attackOption) == false) return false

        return Waiting.waitUntil(2500) { inCombat() } && Waiting.waitUntil(2500) {
            !targetNpc?.hitsplats.isNullOrEmpty()
                    && MyPlayer.getAnimation() != -1
                    && MyPlayer.getAnimation() != animationBeforeAttacking
        }
    }

    override fun resetInventory() {
        Bank.depositInventory()
        Waiting.waitNormal(100,700)

        val randomFoodAmount = Random.nextInt(10, 15)
        logger.debug("[RANDOMIZED FOOD COUNT] > Taking: ${randomFoodAmount}")

        Bank.withdraw("Lobster", randomFoodAmount)
        Bank.close()

        Waiting.waitNormal(1000, 5000)
    }
}