package scripts.wrBlastFurnace

import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintLocation
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.nexus.sdk.mouse.*
import scripts.utils.Logger
import scripts.utils.formatters.Coins
import scripts.utils.formatters.Countdown
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIsOpenNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.payForemanNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.smeltBarsNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.topupCofferNode
import scripts.wrBlastFurnace.behaviours.setup.actions.moveToFurnaceNode
import scripts.wrBlastFurnace.behaviours.setup.validation.EnsurePlayerHasRequirements
import scripts.wrBlastFurnace.behaviours.setup.validation.MoveToFurnaceValidation
import scripts.wrBlastFurnace.managers.*
import java.awt.Color
import java.awt.Font

@TribotScriptManifest(
    name = "WrBlastFurnace Lite 1.2.0",
    description = "Smelts steel bars on the Blast Furnace",
    category = "Smithing",
    author = "WrEcked"
)
class BlastFurnaceScript : TribotScript {
    private val startedAt: Long = System.currentTimeMillis()

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

        val playerRunManager = PlayerRunManager(logger)
        playerRunManager.setNextRunEnablingThreshold()

        //todo enable/disable use of stamina/energy pots
        val staminaManager = StaminaManager(logger, playerRunManager)

        val cameraManager = CameraManager(logger)

        val progressionManager = ProgressionManager(
            logger,
            System.currentTimeMillis(),
            tripStateManager,
            barManager
        )

        /**
         * INITIALIZATION
         * - will setup our player / world controls, options and preferences
         */

        /**
         * Initialise the basic paint
         */
        initPaint(
            progressionManager,
            upkeepManager,
            staminaManager,
            playerRunManager
        )

        val blastFurnaceTree = getBlastTree(
            logger = logger,
            upkeepManager = upkeepManager,
            barManager = barManager,
            tripStateManager = tripStateManager,
            playerRunManager = playerRunManager,
            staminaManager = staminaManager,
            cameraManager = cameraManager
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
        tripStateManager: TripStateManager,
        playerRunManager: PlayerRunManager,
        staminaManager: StaminaManager,
        cameraManager: CameraManager
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
                 * @TODO include support for stopping script when no more resources in bank
                 * - IF re-stocking is disabled OR failed due to no GP
                 * - But for now, without re-stocking, it should gracefully stop.
                 */

                /**
                 * Make sure we're at the Blast Furnace Area
                 */
                selector {
                    condition { MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                    moveToFurnaceNode(logger)
                }

                selector {
                    condition { playerRunManager.satisfiesRunExpectation() }
                    perform {
                        playerRunManager.enableRun()
                        logger.debug("enabled run via default tree...")
                    }
                }

                /**
                 * Serves as a failsafe, as long as the dispenser holds any kind of bar, and we're not in the collecting stage
                 * Ensure that we switch to that state and first withdraw any bars from the dispenser.
                 * This prevents blocking the conveyor belt
                 * - Could happen due to re-starting the script
                 * - Could happen when a script is stopped/ended and is started again
                 * Or any other partial cycle flow occurrence
                 */
                selector {
                    condition { tripStateManager.isCurrentState("COLLECT_BARS") == false }
                    condition { !barManager.dispenserHoldsBars() }
                    perform {
                        tripStateManager.resetCycle("COLLECT_BARS")
                    }
                }

                /**
                 * When necessary, ensure we've paid the foreman to use the furnace
                 * - todo, we could try to combine the foreman and coffer, to only need 1 banking action..
                 */
                selector {
                    condition { upkeepManager.havePaidForeman() }
                    condition { upkeepManager.playerHoldsEnoughCoins() }
                    sequence {
                        ensureIsOpenNode(logger)
                        bankNode(logger, true, false)
                        withdrawItemNode(logger, "Coins", 2500, true)
                        payForemanNode(logger, upkeepManager, tripStateManager, barManager)
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
                        ensureIsOpenNode(logger)
                        bankNode(logger, true, false)

                        withdrawItemNode(logger, "Coins", upkeepManager.getCofferTopupAmount(), true)
                        topupCofferNode(logger, upkeepManager, tripStateManager, barManager)

                        ensureIsOpenNode(logger)
                        bankNode(logger, true, false)
                    }
                }

                selector {
                    condition { !MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                    condition { !upkeepManager.haveFilledCoffer() }
                    condition { !upkeepManager.havePaidForeman() }
                    sequence {
                        smeltBarsNode(
                            logger,
                            barManager,
                            tripStateManager,
                            cameraManager,
                            staminaManager,
                            playerRunManager
                        )
                    }
                }
            }
        }
    }

    private fun initPaint(
        progressionManager: ProgressionManager,
        upkeepManager: UpkeepManager,
        staminaManager: StaminaManager,
        playerRunManager: PlayerRunManager
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
                    .label("Handle Coal")
                    .value { progressionManager.indicateState("PROCESS_COAL") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Handle Ores")
                    .value { progressionManager.indicateState("PROCESS_BASE") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Collect Bars")
                    .value { progressionManager.indicateState("COLLECT_BARS") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Bank Bars")
                    .value { progressionManager.indicateState("BANK_BARS") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Trips")
                    .value {
                        progressionManager.currentTrips()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Forecast")
                    .value {
                        progressionManager.estimatedPerHourTrips()
                    }
                    .build()
            )

        val sidePaint = BasicPaintTemplate.builder()
            .location(PaintLocation.TOP_RIGHT_VIEWPORT)
            .row(
                paintTemplate.toBuilder()
                    .label("Total upkeep spent")
                    .value { Coins().format(upkeepManager.totalSpent) }
                    .build()
            )

        if (upkeepManager.shouldPayForeman()) {
            sidePaint
                .row(
                    paintTemplate.toBuilder()
                        .label("Last foreman payment")
                        .value { Countdown().fromMillis(upkeepManager.lastPaidForemanAt ?: System.currentTimeMillis()) }
                        .build()
                )
        }

        sidePaint
            .row(
                paintTemplate.toBuilder()
                    .label("Sip Stamina")
                    .value { if (staminaManager.satisfiesStaminaState()) "No" else "Yes" }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Re-enable run at")
                    .value(playerRunManager.getNextEnableAtValue().toString().plus("%"))
                    .build()
            )

        Painting.addPaint { mainPaint.build().render(it) }
        Painting.addPaint { sidePaint.build().render(it) }
    }
}