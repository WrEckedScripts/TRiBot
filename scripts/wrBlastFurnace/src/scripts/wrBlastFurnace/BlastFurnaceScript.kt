package scripts.wrBlastFurnace

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.nexus.sdk.mouse.*
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.prevalidation.playerMissesRequirement
import scripts.wrBlastFurnace.trees.getStartupTree

@TribotScriptManifest(
    name = "wrBlastFurnace 1.0.5",
    description = "Performs the Blast Furnace Activity",
    category = "Smithing",
    author = "WrEcked"
)
class BlastFurnaceScript : TribotScript {
    fun initializeMousePainter() {
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

        logger.debug("tree defining")

        if (!Login.isLoggedIn()) {
            val startupTree = getStartupTree(
                logger = logger
            )

            val startupTick = startupTree.tick()
            logger.debug("Resulted startup in: ${startupTick}")
        }

        if (playerMissesRequirement(logger)) {
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


        val blastFurnaceTree = getBlastTree(
            logger = logger
        )

        /**
         * Execute the behaviourTree until the final result is reached.
         */
        val tick = blastFurnaceTree.tick()
        logger.debug("[BLAST] - Behavior Tree TICK result: $tick");
    }

    private fun getBlastTree(
        logger: Logger
    ) = behaviorTree {
        repeatUntil(BehaviorTreeStatus.KILL) {
            selector {
                //..
            }
        }
    }
}