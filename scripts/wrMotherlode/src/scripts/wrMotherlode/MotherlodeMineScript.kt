package scripts.wrMotherlode

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.Skill
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.antiban.RuntimeTracker
import scripts.utils.calculators.ResourceCounter
import scripts.utils.calculators.skills.TrackerCollection
import scripts.utils.failsafes.LastActionTracker
import scripts.utils.failsafes.RepetitiveActionManager
import scripts.utils.mouse.MousePainter
import scripts.utils.progress.webhook.DiscordNotifier
import scripts.wrMotherlode.behaviours.motherlode.getMineTree
import scripts.wrMotherlode.managers.Container
import scripts.wrMotherlode.managers.ProgressionManager
import scripts.wrMotherlode.managers.SackManager
import scripts.wrMotherlode.managers.StateManager
import scripts.wrMotherlode.overlay.OverlayPainter


@TribotScriptManifest(
    name = "WrMotherlodeMine Lite 1.4.0",
    description = "Plays the Motherlode mine for Mining experience and Golden Nuggets",
    category = "Mining",
    author = "WrEcked"
)
class MotherlodeMineScript : TribotScript {
    private val logger: Logger
        get() {
            return Logger("WrMotherlodeMine Lite")
        }

    private val managers: Container = registerManagers(this.logger)

    private fun registerManagers(logger: Logger): Container {
        val repetitiveActionManager = RepetitiveActionManager(logger)
        val progressionManager = ProgressionManager(logger, System.currentTimeMillis())
        val stateManager = StateManager(logger)
        val sackManager = SackManager(logger)

        return Container(
            repetitiveActionManager,
            progressionManager,
            stateManager,
            sackManager
        )
    }

    private fun setupCalculators() {
        TrackerCollection.add(Skill.MINING)

        ResourceCounter.init(
            mapOf(
                "Coal" to 453,
                "Gold ore" to 444,
                "Mithril ore" to 447,
                "Adamantite ore" to 449,
                "Runite ore" to 451,
                "Golden nugget" to null,
                "Pay-dirt" to null
            )
        )
    }

    private fun executeMineTree(logger: Logger, managers: Container) {
        try {
            if (!Login.isLoggedIn()) {
                Waiting.waitUntil(15_000) {
                    Login.login()
                }
            }

            val mineTree = getMineTree(
                logger,
                managers
            )

            val tick = mineTree.tick()
            logger.debug("[Ending] - Reason: $tick")
        } catch (ex: Throwable) {
            handleExecutionError(logger, ex)
        } finally {
            safelyLogout(logger)
        }
    }

    private fun handleExecutionError(logger: Logger, ex: Throwable) {
        logger.error("Error occurred during execution: ${ex.message}")
        ex.printStackTrace()
    }

    private fun safelyLogout(logger: Logger) {
        Waiting.waitUntil(15_000) {
            Login.logout()
        }

        logger.debug("Player logged out successfully.")
    }

    override fun execute(args: String) {
        MousePainter().init()
        OverlayPainter(this.managers).init()

        RuntimeTracker.init()
        RuntimeTracker.initLogger(this.logger)

        FatigueResolver.initLogger(this.logger)

        this.setupCalculators()

        //TODO do not commit.
        DiscordNotifier.initConfig(
            "https://discord.com/api/webhooks/1279066926953402419/VBj8I3sB4Scj73MoV_p1Ei-uUGOCW-b09swi6gKMNVC_o1MsL_eQDkOuyTH47-3a38w-",
            60
        )

        LastActionTracker.track("state")
        executeMineTree(logger, this.managers)
    }
}