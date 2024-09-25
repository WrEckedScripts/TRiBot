package scripts.wrCrafting

import org.tribot.script.sdk.*
import org.tribot.script.sdk.antiban.Antiban
import org.tribot.script.sdk.antiban.PlayerPreferences
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.script.ScriptRuntimeInfo
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger
import scripts.wrCrafting.models.TaskConfiguration
import java.awt.Color
import java.awt.Font
import java.awt.Graphics


@TribotScriptManifest(
    name = "wrCrafting 0.1.1",
    description = "Auto Crafter",
    category = "Crafting",
    author = "WrEcked",
)
class Crafter : TribotScript {
    override fun execute(args: String) {
        val logger = Logger("Main")

        /**
         * SETUP
         * - location
         * - radius around location
         * - Craftable resource name
         */

        //todo randomise the tile
        val grandExchangeCenterTile = WorldTile(3167, 3488, 0);
        val radius = 3
        val grandExchangeArea = Area.fromRadius(grandExchangeCenterTile, radius)
        val taskConfiguration = TaskConfiguration(
            craftableName = "Uncut emerald",
            craftedName = "Emerald"
        )

        /**
         * Setup manager classes
         */
        val logisticsManager = LogisticsManager(grandExchangeArea)
        val progressManager = ProgressManager(Skill.CRAFTING, Skill.CRAFTING.xp)
        val suppliesManager = SuppliesManager(taskConfiguration, progressManager)
        val craftingManager = CraftingManager(taskConfiguration)

        /**
         * Initialise the basic paint
         */
        initPaint(progressManager)

        // Let's highlight the tile we're working from
        paintTile(grandExchangeCenterTile)

        logger.debug("tree defining")
        /**
         * Define the Crafting BehaviourTree
         */
        val behaviorTree = getCraftingTrees(
            logisticsManager = logisticsManager,
            suppliesManager = suppliesManager,
            craftingManager = craftingManager,
            logger = logger
        )

        logger.debug("tree defined")

        /**
         * Execute the behaviourTree until the final result is reached.
         */
        val tick = behaviorTree.tick()
        logger.debug("Behavior Tree TICK result: $tick");
    }

    private fun getCraftingTrees(
        logisticsManager: LogisticsManager,
        suppliesManager: SuppliesManager,
        craftingManager: CraftingManager,
        logger: Logger
    ) = behaviorTree {
        repeatUntil(BehaviorTreeStatus.KILL) {
            selector {
                // region Login & Locational sequences
                /**
                 * Sequence Login
                 * - Makes sure the user is fully logged into the RS world
                 */
                sequence {
                    condition { !Login.isLoggedIn() }
                    perform { logger.debug("Waiting until logged in") }
                    perform { Login.login() }
                }

                /**
                 * Sequence Location
                 * - Ensures the player is within the skilling area defined
                 * - Walks towards the area
                 *
                 * TODO:
                 * - Enable anywhere to start
                 * - - Determine distance between skilling area
                 * - - if greater than X:
                 * - - walk to nearest bank
                 * - - grab teleportation option available
                 * - - - varrock tablet / ring of wealth (charged)
                 * - - - teleport to the g.e / varrock town
                 */
                //todo no need to keep on running this right? executed every god damn milisecond
                sequence {
                    condition { !logisticsManager.inSkillingArea() }
                    perform { logger.debug("Walking to skilling area") }
                    perform { GlobalWalking.walkTo(logisticsManager.skillingArea.center) }
                }


                // endregion

                /**
                 * TODO idea:
                 * - Separate chisel fetching and craftables fetching to own sequences
                 * - Closing of banks is a sequence, after we've done the chisel and craftables
                 * - - Only if a bank is open of course
                 * - This above could be a fix to the false-positives we've seen, where we'd re-open the bank quickly
                 */

                /**
                 * Ensure we've got a chisel in our inventory for gem cutting
                 */
                sequence {
                    condition {
                        suppliesManager.needsChisel()
                    }
                    selector {
                        condition { Bank.ensureOpen() } //todo can this be a simple isOpen check?
                        sequence {
                            perform { GlobalWalking.walkToBank() }
                            perform { Bank.ensureOpen() }
                        }
                    }
                    perform {
                        Waiting.waitUntil{
                            suppliesManager.depositUnusableItems()
                            suppliesManager.withdrawChiselFromBank()
                        }

                        craftingManager.taskConfiguration.isProcessing = false
                    }
                }

                /**
                 * Sequence: Bank of required items
                 * - If we do not hold a chisel, bank and withdraw one
                 * - AND/OR if we do not have any craftables, bank and withdraw full inv
                 */
                sequence {
                    condition {
                        suppliesManager.needsCraftables()
                    }
                    selector {
                        condition { Bank.ensureOpen() }
                        sequence {
                            perform { GlobalWalking.walkToBank() }
                            perform { Bank.ensureOpen() }
                        }
                    }

                    perform {
                        Waiting.waitUntil {
                            suppliesManager.depositUnusableItems()
                            suppliesManager.withdrawCraftablesFromBank()
                        }

                        craftingManager.taskConfiguration.isProcessing = false
                    }
                }

                /**
                 * Start crafting
                 * - If we're not already in a processing state
                 * - If we don't need a chisel
                 * - If we don't need any craftables
                 */
                sequence {
                    condition { ! craftingManager.taskConfiguration.isProcessing }
                    condition { ! suppliesManager.needsChisel() }
                    condition { ! suppliesManager.needsCraftables() }
                    perform {
                        craftingManager.initCrafting()
                    }
                }

                /**
                 * Sequence: Chat pop-up, Level-up related
                 * - Make sure to, when a "click continue" is in chat, we click it and restart crafting again.
                 * - Antiban approach / profile unique/random: don't click but just re-init crafting
                 */
                sequence {
                    //todo any condition we could use here?
                    // perhaps non animating character.
                    perform {
                        //todo untested, need to see if levelup now gets closed
                        // and we continue with cutting gems
                        if (ChatScreen.isClickContinueOpen()) {
                            //todo in favor of antiban, sometimes click, sometimes just re-init crafting?
                            // perhaps player preferences class could be used here.
                            Waiting.waitNormal(1000, 4000)
                            ChatScreen.clickContinue()
                            craftingManager.initCrafting()
                        }
                    }
                }

                // todo SEQUENCE antiban, open random widget/inspect skills/hover random skill/hover random item or tile
                //todo sequence for keeping track last animation/action to determine if we're somehow stuck/inactive
                // - should initiate logout/scriptkill
            }
        }
    }

    private fun paintTile(tile: WorldTile) {
        Painting.addPaint { g: Graphics ->
            g.color = Color.green
            val boundsToDraw = tile.bounds
            val polygon = boundsToDraw.get()

            if (boundsToDraw.isPresent) {
                g.drawPolygon(polygon)
            }
        }
    }

    private fun initPaint(progressManager: ProgressManager) {
        val paintTemplate = PaintTextRow.builder()
            .background(Color(62, 62, 62))
            .font(Font("Segoe UI", 0, 12))
            .build()

        val mainPaint = BasicPaintTemplate.builder()
            .row(PaintRows.scriptName(paintTemplate.toBuilder()))
            .row(PaintRows.runtime(paintTemplate.toBuilder()))
            .row(
                paintTemplate.toBuilder()
                    .label("Crafted")
                    .value { progressManager.getCraftedItemsCount() }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("EXP")
                    .value {
                        progressManager.experiencePaintValue()
                    }
                    .build()
            )
            .build()

        Painting.addPaint { mainPaint.render(it) }
    }


}