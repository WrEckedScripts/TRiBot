package scripts.wrCrafting

import org.tribot.script.sdk.*
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
import scripts.utils.gui.components.GuiState
import scripts.utils.gui.components.ScriptGui
import scripts.wrCrafting.gui.scriptGui
import java.awt.Color
import java.awt.Font
import java.awt.Graphics


@TribotScriptManifest(
    name = "wrCrafting 0.0.8",
    description = "Auto Crafter",
    category = "Crafting",
    author = "WrEcked",
)
class Crafter : TribotScript {
    override fun execute(args: String) {
        val logger = Logger(ScriptRuntimeInfo.getScriptName())
        logger.debug("Started")

        initPaint()
        logger.debug("Painted!")

        val scriptGui = ScriptGui(scriptGui)
        logger.error("SHOWTIME!: " + scriptGui)
        if(! scriptGui.equals(GuiState.Completed)){
            Log.error("You didn't complete the GUI")
            return
        }

        val grandExchangeCenterTile = WorldTile(3167, 3488, 0);
        val radius = 3
        val grandExchangeArea = Area.fromRadius(grandExchangeCenterTile, radius)

        val craftableName = "Uncut emerald"

        val logisticsManager = LogisticsManager(logger, grandExchangeArea)
        val suppliesManager = SuppliesManager(logger, craftableName)
        val craftingManager = CraftingManager(logger, craftableName)

        paintTile(grandExchangeCenterTile)
        logger.debug("Painted location-tile")

        //todo await until GUI rendered and filled.

        val behaviorTree = getCraftingTrees(
            logisticsManager = logisticsManager,
            suppliesManager = suppliesManager,
            craftingManager = craftingManager,
            logger = logger
        )

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
            logger.debug("repeatUntill KILL status given:")
            selector {
                // Make sure the character is logged in or log them in
                sequence {
                    condition { !Login.isLoggedIn() }
                    perform { logger.debug("Waiting until logged in") }
                    perform { Login.login() }
                }

                // Make sure we're in the skilling area, or move there.
                sequence {
                    condition { !logisticsManager.inSkillingArea() }
                    perform { logger.debug("Walking to skilling area") }
                    perform { GlobalWalking.walkTo(logisticsManager.skillingArea.center) }
                }

                sequence {
                    condition {
                        Waiting.waitUntil {
                            suppliesManager.needsChisel() || suppliesManager.needsCraftables()
                        }
                    }
                    selector {
                        condition { Bank.ensureOpen() }
                        sequence {
                            perform { GlobalWalking.walkToBank() }
                            perform { Bank.ensureOpen() }
                        }
                    }
                    perform {
                        // Added waiting.waituntil, as i've seen these two become false here?
                        logger.debug("CHISEL: " + suppliesManager.needsChisel())
                        logger.debug("GEMS  : " + suppliesManager.needsCraftables())
                        logger.debug("banking time");

                        Query.inventory()
                            .nameNotEquals(
                                "Chisel",
                                craftingManager.craftableName
                            )
                            .forEach { item ->
                                Bank.depositAll(item.name)

                                //todo Wait until it's banked?
                                Waiting.waitUntil {
                                    Query.inventory()
                                        .nameEquals(item.name)
                                        .findRandom()
                                        .isEmpty
                                }
                            }

                        if (suppliesManager.needsChisel()) {
                            logger.debug("need a chisel")
                            Waiting.waitUntil {
                                suppliesManager.withdrawChiselFromBank()
                            }
                            logger.debug("got a chisel")
                        }

                        if (suppliesManager.needsCraftables()) {
                            logger.debug("need craftables")
                            Waiting.waitUntil {
                                suppliesManager.withdrawCraftablesFromBank()
                            }
                            logger.debug("got craftables")
                        }

                        Bank.close()
                        // Reset the state, to re-trigger the last sequence
                        craftingManager.isProcessing = false
                    }
                }
                sequence {
                    //todo seems to be overkill / not valuable to check for a complete inv
                    // only this condition, causes spam using chisel
                    condition { !suppliesManager.hasCompletedInventory() }
                    // this actually re-triggers the sequence
                    condition { !craftingManager.isProcessing }
                    perform {
                        logger.debug("crafting sequence")
                        Waiting.waitUntil {
                            craftingManager.initCrafting()
                            // todo antiban, leave screen sometimes
                        }
                    }
                }
                sequence {
                    //todo any condition we could use here?
                    // perhaps non animating character.
                    perform {
                        logger.debug("Checking for stale char, due to interface pop-up?")
                        Waiting.waitNormal(2000, 4000)

                        //todo untested, need to see if levelup now gets closed
                        // and we continue with cutting gems
                        if (ChatScreen.isClickContinueOpen()) {
                            //todo in favor of antiban, sometimes click, sometimes just re-init crafting?
                            // perhaps player preferences class could be used here.

                            ChatScreen.clickContinue()
                            craftingManager.initCrafting()
                        }
                    }
                }
                //todo antiban, open random widget/inspect skills/hover random skill/hover random item or tile
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

    private fun initPaint() {
        val paintTemplate = PaintTextRow.builder()
            .background(Color(62, 62, 62))
            .font(Font("Segoe UI", 0, 12))
            .build()

        val mainPaint = BasicPaintTemplate.builder()
            .row(PaintRows.scriptName(paintTemplate.toBuilder()))
            .row(PaintRows.runtime(paintTemplate.toBuilder()))
            .row(
                paintTemplate.toBuilder()
                    .label("Items crafted")
                    .value { 0 }
                    .build()
            )
            .build()

        Painting.addPaint { mainPaint.render(it) }
    }

}