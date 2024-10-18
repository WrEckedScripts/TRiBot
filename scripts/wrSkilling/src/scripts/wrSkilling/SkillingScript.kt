package scripts.wrSkilling

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.mouse.MousePainter
import scripts.wrSkilling.behaviours.crafting.LeatherTree
import scripts.wrSkilling.managers.Container
import scripts.wrSkilling.managers.StateManager

@TribotScriptManifest(
    name = "WrSkilling 1.0.0",
    description = "Helps prepare for Blast Furnace account requirements",
    category = "Tools",
    author = "WrEcked"
)
class SkillingScript : TribotScript {
    private val logger: Logger
        get() {
            return Logger("WrSkilling")
        }

    private val managers: Container = registerManagers(this.logger)

    private fun registerManagers(logger: Logger): Container {
        val stateManager = StateManager(logger)

        return Container(
            stateManager
        )
    }

    private fun setupHelpers() {
        Lottery.initLogger(this.logger)
    }


    override fun execute(args: String) {
        logger.info("Started with args: $args")

        setupHelpers()

        MousePainter().init()

        //TODO overlay

        executeLeatherTree(logger, this.managers)
    }

    private fun executeLeatherTree(logger: Logger, managers: Container) {
        if (!Login.isLoggedIn()) {
            Waiting.waitUntil(5_000) {
                Login.login()
            }
        }

        val leatherTree = LeatherTree(
            logger = logger,
            managers = managers
        )

        val tick = leatherTree.tick()
        logger.error("[Ending] - Reason: $tick")
    }
}