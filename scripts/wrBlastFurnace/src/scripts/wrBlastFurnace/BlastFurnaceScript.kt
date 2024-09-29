package scripts.wrBlastFurnace

import org.tribot.script.sdk.Chatbox
import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.ScriptListening
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintLocation
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.nexus.sdk.mouse.*
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.formatters.Coins
import scripts.utils.formatters.Countdown
import scripts.utils.progress.webhook.DiscordNotifier
import scripts.wrBlastFurnace.behaviours.furnace.getBlastTree
import scripts.wrBlastFurnace.behaviours.setup.validation.EnsurePlayerHasRequirements
import scripts.wrBlastFurnace.managers.*
import java.awt.Color
import java.awt.Font

@TribotScriptManifest(
    name = "WrBlastFurnace Lite 1.3.4",
    description = "Smelts steel bars on the Blast Furnace",
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
        val logger = Logger("WrBlastFurnace Lite")

        this.initializeMousePainter()

        ScriptListening.addEndingListener {
            DiscordNotifier.notify(
                true,
                "Instance ended, Your account (${MyPlayer.getUsername()}) is no longer running!",
                0xFF1E61
            )
        }

        val playerMissesRequirements = EnsurePlayerHasRequirements(logger)
            .playerMissesRequirement()

        if (playerMissesRequirements) {
            logger.error("Stopping script, we're missing requirements...")
            return
        }

        logger.info("Requirement check completed")

        val upkeepManager = UpkeepManager(logger)
        val dispenserManager = DispenserManager(logger)
        val meltingPotManager = MeltingPotManager(logger)
        val tripStateManager = TripStateManager(logger)

        val playerRunManager = PlayerRunManager(logger)
        playerRunManager.setNextRunEnablingThreshold()

        val staminaManager = StaminaManager(logger, playerRunManager)
        val cameraManager = CameraManager(logger)
        val progressionManager = ProgressionManager(
            logger,
            System.currentTimeMillis(),
            tripStateManager,
        )

        Lottery.initLogger(logger)

        /**
         * Initialise the basic paint
         */
        initPaint(
            progressionManager,
            upkeepManager,
            staminaManager,
            playerRunManager
        )

//        try {
            val blastFurnaceTree = getBlastTree(
                logger = logger,
                upkeepManager = upkeepManager,
                dispenserManager = dispenserManager,
                meltingPotManager = meltingPotManager,
                tripStateManager = tripStateManager,
                playerRunManager = playerRunManager,
                staminaManager = staminaManager,
                cameraManager = cameraManager
            )

        Chatbox.hide() //todo GUI option + dedicated place within the tree.

            /**
             * Execute the behaviourTree until the final result is reached.
             */
            val tick = blastFurnaceTree.tick()
            logger.debug("[Ending] - Reason: $tick");

        Login.logout()
//        } catch (ex: Throwable) {
//            logger.error(ex.message)
//            logger.trace(ex)
//        }
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
            .row(
                paintTemplate.toBuilder()
                    .label("Gross earned")
                    .value {
                        progressionManager.grossProfit()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Spent")
                    .value { progressionManager.currentSpent() }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Net earned")
                    .value { progressionManager.netProfit() }
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