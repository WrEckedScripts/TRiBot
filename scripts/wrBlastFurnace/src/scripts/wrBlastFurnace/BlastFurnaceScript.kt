package scripts.wrBlastFurnace

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
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
import scripts.wrBlastFurnace.managers.UpkeepManager
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

        /**
         * Initialise the basic paint
         */
//        initPaint(progressManager)

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

        val blastFurnaceTree = getBlastTree(
            logger = logger,
            upkeepManager = upkeepManager
        )

        /**
         * Execute the behaviourTree until the final result is reached.
         */
        val tick = blastFurnaceTree.tick()
        logger.debug("[BLAST] - Behavior Tree TICK result: $tick");
    }

    private fun getBlastTree(
        logger: Logger,
        upkeepManager: UpkeepManager
    ) = behaviorTree {
        repeatUntil(BehaviorTreeStatus.KILL) {
            sequence {
                perform {
                    logger.debug(
                        "lastPaidAt: ${upkeepManager.lastPaidForemanAt}" +
                        "holdEnfCoins: ${upkeepManager.playerHoldsEnoughCoins()}"
                    )
                }
                selector {
                    condition { MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                    moveToFurnaceNode(logger)
                }

                selector {
                    condition { upkeepManager.havePaidForeman() }
                    condition { upkeepManager.playerHoldsEnoughCoins() }
                    sequence {
                        bankNode(logger)
                        //todo, withdraw shouldn't be a node.. it's simply an action to execute at this point.
                        withdrawItemNode(logger, "Coins", 2500)
                        payForemanNode(logger, upkeepManager)
                    }
                }

//                selector {
//                    condition { !upkeepManager.playerHoldsEnoughCoins(logger) }
//                    payForemanNode(logger)
//                }


//                selector {
//                    condition { !MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() && !upkeepManager.shouldTopupCoffer() }
//                    perform {
//                        logger.error("COFFERTOPUP")
//                    }
//                    topupCofferNode(logger) //todo add upkeepManager for both coffer and foreman (below 60 smith)
//                }
//
//                selector {
//                    condition { !MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
//                    perform {
//                        logger.error("SMELTBARS")
//                    }
////                    smeltBarsNode(logger) //todo add BarManager
//                }
            }
        }
    }
}