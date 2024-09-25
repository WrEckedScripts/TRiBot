package scripts.wrCombat

import javafx.scene.layout.TilePane
import org.tribot.script.sdk.*
import org.tribot.script.sdk.antiban.Antiban
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.script.ScriptRuntimeInfo
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.GlobalWalking
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Paint
import kotlin.jvm.optionals.getOrNull

@TribotScriptManifest(
    name = "wrCombat",
    description = "Killing script",
    category = "Combat",
    author = "WrEcked"
)
class CombatScript : TribotScript {
    private var combatArea: Area? = null
    private val npcNames: MutableList<String> = mutableListOf()
    private var combatManager: CombatManager? = null
    private var lootingManager: LootingManager? = null

    // How the script will work.
    // The combat script will go to a pre-defined area and attack npcs.
    // If we need to bank, the bot will go to the bank and deposit/withdraw accordingly.
    override fun execute(args: String) {
        val logger = Logger(ScriptRuntimeInfo.getScriptName())
        logger.debug("Let's go killing with [${ScriptRuntimeInfo.getScriptName()}}]")

        // Painting time
        val scriptPaintTemplate = PaintTextRow.builder()
            .background(Color(62, 62, 62))
            .font(Font("Segoe UI", 0, 12))
            .build()

        val scriptMainPaint = BasicPaintTemplate.builder()
            .row(PaintRows.scriptName(scriptPaintTemplate.toBuilder()))
            .row(PaintRows.runtime(scriptPaintTemplate.toBuilder()))
            .row(
                scriptPaintTemplate
                    .toBuilder()
                    .label("Kill count")
                    .value { combatManager?.killCount }
                    .build()
            )
            .build()

        Painting.addPaint { scriptMainPaint.render(it) }

        // Target for killing and where
        val npcName = "Guard"
        val faladorGuardCenterTile = WorldTile(2966, 3388, 0)
        val radius = 50

        combatArea = Area.fromRadius(faladorGuardCenterTile, radius)
        npcNames.add(npcName)
        combatManager = CombatManager(combatArea!!, npcNames)
        lootingManager = LootingManager(combatArea!!)


        logger.debug("Combat area: $combatArea")
        logger.debug("Npc names: ${npcNames.joinToString(", ") { it }}")

        val behaviorTree = getCombatTrees(
            combatManager = combatManager!!,
            lootingManager = lootingManager!!,
            logger = logger
        )

        val tick = behaviorTree.tick()
        logger.debug("Behavior tree tick result: $tick")
    }

    private fun getCombatTrees(
        combatManager: CombatManager,
        lootingManager: LootingManager,
        logger: Logger
    ) = behaviorTree {
        repeatUntil(BehaviorTreeStatus.KILL) {
            logger.debug("repeatUntil kill:");
            selector {
                // Ensure that the player is logged in
                sequence {
                    condition { !Login.isLoggedIn() }
                    perform { logger.debug("Logging in to the game") }
                    perform { Login.login() }
                }

                // Ensure run is activate
                sequence {
                    condition { Antiban.shouldTurnOnRun() }
                    condition { !Options.isRunEnabled() }
                    perform { logger.debug("Enabling run") }
                    perform { Options.setRunEnabled(true) }
                }

                // Ensure inventory is not full sequence
                // Or ensure that we have eatable food within our Inventory
                sequence {
                    condition { Inventory.isFull() }
                    //todo this needs to be validated
                    condition {
                        Query.inventory()
                            .actionContains("Eat")
                            .isNotNoted
                            .findFirst()
                            .getOrNull() === null
                    }
                    //end todo

                    perform { logger.debug("Banking") }
                    selector {
                        condition { Bank.ensureOpen() }
                        sequence {
                            perform { GlobalWalking.walkToBank() }
                            perform { Bank.ensureOpen() }
                        }
                    }
                    perform { Bank.depositInventory() }
//                    perform {
//                        todo fetch food from bank
//                    }
                }

                // Ensure in combat area sequence
                sequence {
                    condition { !combatManager.inCombatArea() }
                    perform { logger.debug("Walking to combat area center") }
                    perform { GlobalWalking.walkTo(combatManager.combatArea.center) }
                }

                // Ensure target is not null
                sequence {
                    condition { combatManager.isTargetNull() }
                    perform { logger.debug("Initializing new target npc") }
                    perform { combatManager.setNewTargetNpc() }
                }

                // Ensure target is valid
                sequence {
                    condition { combatManager.isTargetNotValid() }
                    perform { logger.debug("Target npc has been neutralized") }
                    perform { combatManager.incKillCount() }
                    perform { combatManager.setNewTargetNpc() }
                }

                // Consume any food when the players HP is low
                sequence {
                    condition { MyPlayer.getCurrentHealthPercent() <= 50 }
                    perform {
                        Query.inventory()
                            .actionContains("Eat")
                            .isNotNoted
                            .findFirst()
                            .getOrNull()
                            //https://kotlinlang.org/docs/scope-functions.html#lambda-result (about "let" and "it")
                            ?.let { it.click("Eat") }
                    }
                }

                // Perform any looting when the player is not in combat
                sequence {
                    condition { lootingManager.shouldLoot }
                    condition { !combatManager.inCombat() }
                    perform { lootingManager.manageLooting() }
                }

                logger.debug("after loot:");

                // Bury any bones that were picked up when the player is not in combat
                sequence {
                    condition { Inventory.getAll().any { it.actions.contains("Bury") && !it.definition.isNoted } }
                    condition { !combatManager.inCombat() }
                    perform { logger.debug("Burying bones found inside the inventory") }
                    perform {
                        Inventory.getAll().filter { it.actions.contains("Bury") && !it.definition.isNoted }
                            .forEach { bone ->
                                val startCount = Inventory.getCount(bone.id)
                                val click = bone.click("Bury")
                                if (!click) return@forEach
                                if (!Waiting.waitUntil(2500) { Inventory.getCount(bone.id) < startCount }) return@forEach
                            }
                    }
                }

                logger.debug("after bury/loot bones:");

                // No need to attack if already in combat
                condition { combatManager.inCombat() }

                logger.debug("after incombat check:");

                // Not in combat, attack the target npc
                perform { combatManager.attackTargetNpc() }
                logger.debug("after attack target:");
            }
        }
    }
}