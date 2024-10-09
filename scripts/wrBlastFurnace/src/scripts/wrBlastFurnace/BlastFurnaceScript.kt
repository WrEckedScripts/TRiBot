package scripts.wrBlastFurnace

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.ScriptListening
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.mouse.MousePainter
import scripts.utils.progress.webhook.DiscordNotifier
import scripts.wrBlastFurnace.behaviours.furnace.getBlastTree
import scripts.wrBlastFurnace.behaviours.setup.validation.EnsurePlayerHasRequirements
import scripts.wrBlastFurnace.gui.GUI
import scripts.wrBlastFurnace.gui.Settings
import scripts.wrBlastFurnace.managers.*
import scripts.wrBlastFurnace.overlay.OverlayPainter
import java.util.concurrent.CompletableFuture

@TribotScriptManifest(
    name = "WrBlastFurnace Lite 1.4.2",
    description = "Smelts bronze, iron and steel bars on the Blast Furnace. Please visit the forums / our Discord for a detailed list of requirements and guidance.",
    category = "Smithing",
    author = "WrEcked"
)
class BlastFurnaceScript : TribotScript {
    private val logger: Logger
        get() {
            return Logger("WrBlastFurnace Lite")
        }

    private val managers: Container = registerManagers(this.logger)

    /**
     * Helper singletons, for randomization and progress updating (discord)
     */
    private fun setupHelpers() {
        Lottery.initLogger(this.logger)

        DiscordNotifier.initLogger(this.logger)
        DiscordNotifier.initConfig(Settings.discordUrl, Settings.interval.toInt())
    }

    override fun execute(args: String) {
        MousePainter().init()

        OverlayPainter(
            this.managers.progressionManager,
            this.managers.upkeepManager,
            this.managers.staminaManager,
            this.managers.playerRunManager
        ).init()

        setupNotifications()
        setupHelpers()

        val closed = CompletableFuture<Unit>()
        val started = CompletableFuture<Unit>()
        startGui(started, closed)

        started.thenAccept {
            logger.debug("[GUI] - Completed, let's go blast furnacing!")

            executeBlastFurnaceTree(logger, this.managers)
        }

        closed.thenAccept {
            logger.debug("[GUI] - Closed, without starting, we'll meet again :)")
        }
    }

    private fun registerManagers(logger: Logger): Container {
        val upkeepManager = UpkeepManager(logger)
        val dispenserManager = DispenserManager(logger)
        val meltingPotManager = MeltingPotManager(logger)
        val tripStateManager = TripStateManager(logger)
        val playerRunManager = PlayerRunManager(logger).apply { setNextRunEnablingThreshold() }
        val staminaManager = StaminaManager(logger, playerRunManager)
        val cameraManager = CameraManager(logger)
        val progressionManager = ProgressionManager(logger, System.currentTimeMillis(), tripStateManager)

        return Container(
            upkeepManager,
            dispenserManager,
            meltingPotManager,
            tripStateManager,
            playerRunManager,
            staminaManager,
            cameraManager,
            progressionManager
        )
    }

    private fun setupNotifications() {
        ScriptListening.addEndingListener {
            DiscordNotifier.notify(
                true,
                "Instance ended, Your account (${MyPlayer.getUsername()}) is no longer running!",
                0xFF1E61
            )

            /**
             * Wait so we give the DiscordNotifier some time to grab a screenshot
             * Before continue-ing to stop the script
             */
            Waiting.wait(4_000)
        }
    }

    private fun executeBlastFurnaceTree(logger: Logger, managers: Container) {
        try {
            if (!Login.isLoggedIn()) {
                Waiting.waitUntil(5_000) {
                    Login.login()
                }
            }

            ensurePlayerHasRequirements(logger)

            val blastFurnaceTree = getBlastTree(
                logger = logger,
                managers = managers
            )

            val tick = blastFurnaceTree.tick()
            logger.debug("[Ending] - Reason: $tick")

        } catch (ex: Throwable) {
            handleExecutionError(logger, ex)

        } finally {
            safelyLogout(logger)
        }
    }

    private fun ensurePlayerHasRequirements(logger: Logger) {
        val playerMissesRequirements = EnsurePlayerHasRequirements(logger)
            .playerMissesRequirement()

        if (playerMissesRequirements) {
            throw Exception("Stopping script, we're missing requirements...")
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

    private fun startGui(guiStarted: CompletableFuture<Unit>, guiClosed: CompletableFuture<Unit>) {
        application(exitProcessOnExit = false) {
            val guiRunning = remember { mutableStateOf(true) }
            if (guiRunning.value) {
                Window(
                    onCloseRequest = {
                        guiRunning.value = false
                        guiClosed.complete(Unit)
                    },
                    title = "WrBlastFurnace Lite GUI",
                    resizable = true,
                    state = WindowState(width = 400.dp, height = 625.dp)
                ) {
                    val gui = GUI(onStartScript = {
                        guiRunning.value = false
                        guiStarted.complete(Unit)
                    })
                    gui.App()
                }
            }
        }
    }
}
