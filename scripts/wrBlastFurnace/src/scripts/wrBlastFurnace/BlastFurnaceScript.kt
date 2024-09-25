package scripts.wrBlastFurnace

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.nexus.sdk.mouse.*
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.payForemanNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.smeltBarsNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.topupCofferNode
import scripts.wrBlastFurnace.behaviours.setup.actions.moveToFurnaceNode
import scripts.wrBlastFurnace.behaviours.setup.validation.EnsurePlayerHasRequirements
import scripts.wrBlastFurnace.behaviours.setup.validation.MoveToFurnaceValidation
import scripts.wrBlastFurnace.managers.BarManager
import scripts.wrBlastFurnace.managers.TripStateManager
import scripts.wrBlastFurnace.managers.UpkeepManager
import java.awt.Color
import java.awt.Font
import kotlin.jvm.optionals.getOrNull

@TribotScriptManifest(
    name = "wrBlastFurnace 1.0.6",
    description = "Performs the Blast Furnace Activity",
    category = "Smithing",
    author = "WrEcked"
)
class BlastFurnaceScript : TribotScript {
    private fun initializeMousePainter() {
        // Create configurations for the mouse paint components
        val mouseCursorPaintConfig = MouseCursorPaintConfig()
        val mouseSplinePaintConfig = MouseSplinePaintConfig()
        val mouseRipplePaintConfig = MouseRipplePaintConfig()

        // Choose a specific MouseCursorPaint implementation
        val mouseCursorPaint = MouseCursorPaint(mouseCursorPaintConfig)
        // Or use another implementation like OriginalMouseCursorPaint or PlusSignMouseCursorPaint
        // val mouseCursorPaint = OriginalMouseCursorPaint(mouseCursorPaintConfig)
        // val mouseCursorPaint = PlusSignMouseCursorPaint(mouseCursorPaintConfig)

        // Create the MousePaintThread instance with the configurations
        val mousePaintThread = MousePaintThread(
            mouseSplinePaintConfig,
            mouseCursorPaintConfig,
            mouseRipplePaintConfig,
            mouseCursorPaint
        )

        // Start the MousePaintThread
        mousePaintThread.start()
    }

    override fun execute(args: String) {
        val logger = Logger("Main")

        this.initializeMousePainter()

        /**
         * Setup manager classes
         */

//        logger.debug("tree defining")
//
//        val startupTree = getStartupTree(
//            logger = logger
//        )

//        val startupTick = startupTree.tick()
//        logger.debug("Resulted startup in: ${startupTick}")

        val playerMissesRequirements = EnsurePlayerHasRequirements(logger)
            .playerMissesRequirement()

        if (playerMissesRequirements) {
            logger.error("Stopping script, we're missing requirements...")
            return
        }

        logger.info("Requirement check completed")

        // Prepare Tree
        // - Move to g.e.
        // -    if not at g.e. find nearest bank for ring of wealth
        // -    if no ring of wealth, find varrock tab
        // -    if no varrock tab
        // -    start walking

        // - Determine if we need 1-dose stamina's
        // -    if we have, ensure we got a random amount between 10,40
        // -    if we don't have 1-dose's but have other doses, let's decant some
        // -    if we can't decant, let's buy them

        // - Determine if we need ores
        // -    based on the level for now?

        val upkeepManager = UpkeepManager(logger)

        val barManager = BarManager(logger)

        val tripStateManager = TripStateManager(logger)

        /**
         * Initialise the basic paint
         */
        initPaint(tripStateManager, upkeepManager)

        val blastFurnaceTree = getBlastTree(
            logger = logger,
            upkeepManager = upkeepManager,
            barManager = barManager,
            tripStateManager = tripStateManager
        )

        /**
         * Execute the behaviourTree until the final result is reached.
         */
        val tick = blastFurnaceTree.tick()
        logger.debug("[BLAST] - Behavior Tree TICK result: $tick");
    }

    private fun getBlastTree(
        logger: Logger,
        upkeepManager: UpkeepManager,
        barManager: BarManager,
        tripStateManager: TripStateManager
    ) = behaviorTree {
        repeatUntil(BehaviorTreeStatus.KILL) {
            sequence {

                /**
                 * @TODO implement a cycleFailSafeNode
                 * - That, based on a set of conditionals, resets the cycle back to a specific case
                 * - This should be taken the highest priority of the tree, by doing this at the top here
                 * - I believe it should do so.
                 */

                /**
                 * @TODO implement playerState checkups
                 * - Re-enable run
                 * - sip stamina on next bank trip (to add in bankNode)
                 * - other stuff?
                 */

                /**
                 * Make sure we're at the Blast Furnace Area
                 */
                selector {
                    condition { MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                    moveToFurnaceNode(logger)
                }

                /**
                 * When necessary, ensure we've paid the foreman to use the furnace
                 * - todo, we could try to combine the foreman and coffer, to only need 1 banking action..
                 */
                selector {
                    condition { upkeepManager.havePaidForeman() }
                    condition { upkeepManager.playerHoldsEnoughCoins() }
                    sequence {
                        bankNode(logger, true)
                        withdrawItemNode(logger, "Coins", 2500, true)
                        payForemanNode(logger, upkeepManager, tripStateManager)
                    }
                }

                /**
                 * Ensures the coffer remains filled.
                 * - todo, we could try to combine the foreman and coffer, to only need 1 banking action..
                 */
                selector {
                    condition { upkeepManager.haveFilledCoffer() }
                    condition { upkeepManager.playerHoldsEnoughCoins(upkeepManager.getCofferTopupAmount()) }
                    sequence {
                        bankNode(logger, true)
                        withdrawItemNode(logger, "Coins", upkeepManager.getCofferTopupAmount(), true)
                        topupCofferNode(logger, upkeepManager, tripStateManager)
                        bankNode(logger, true)
                    }
                }
//
                selector {
                    condition { !MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                    condition { !upkeepManager.haveFilledCoffer() }
                    condition { !upkeepManager.havePaidForeman() }
                    sequence {
                        smeltBarsNode(logger, barManager, tripStateManager)
                    }
                }
            }
        }
    }

    private fun initPaint(
        tripStateManager: TripStateManager,
        upkeepManager: UpkeepManager
    ) {
        val paintTemplate = PaintTextRow.builder()
            .background(Color(62, 62, 62))
            .font(Font("Segoe UI", 0, 12))
            .build()

        val mainPaint = BasicPaintTemplate.builder()
            .row(PaintRows.scriptName(paintTemplate.toBuilder()))
            .row(PaintRows.runtime(paintTemplate.toBuilder()))
            .row(
                paintTemplate.toBuilder()
                    .label("Collect Ores")
                    .value { if(tripStateManager.isCurrentState("COLLECT_ORES") == false) "<---" else ""}
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Fill Conveyor")
                    .value { if(tripStateManager.isCurrentState("FILL_CONVEYOR") == false) "<---" else ""}
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Collect Bars")
                    .value { if(tripStateManager.isCurrentState("COLLECT_BARS") == false) "<---" else ""}

                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Bank Bars")
                    .value { if(tripStateManager.isCurrentState("BANK_BARS") == false) "<---" else ""}
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Trips")
                    .value { tripStateManager.tripCount.toString().plus("| Bars (${tripStateManager.tripCount * 14})") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Total upkeep spent")
                    .value { formatCoins(upkeepManager.totalSpent)  }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Last foreman payment")
                    .value { formatMillisToCountdown(upkeepManager.lastPaidForemanAt ?: System.currentTimeMillis()) }
                    .build()
            )
            .build()

        Painting.addPaint { mainPaint.render(it) }
    }

    fun formatMillisToCountdown(milliseconds: Long): String {
        // Calculate total seconds
        val nextPayTime = (System.currentTimeMillis() - milliseconds)
        val totalSeconds = (nextPayTime / 1000).toInt()

        // Calculate minutes and seconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        // Format minutes and seconds as "MM:SS"
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatCoins(coins: Int): String {
        return when {
            coins < 1000 -> coins.toString()
            coins in 1000..9999 -> String.format("%.2fk", coins / 1000.0)
            coins in 10000..9999999 -> String.format("%.2fM", coins / 1000000.0)
            else -> coins.toString() // For values 10 million and above, you can adjust as needed
        }
    }
}