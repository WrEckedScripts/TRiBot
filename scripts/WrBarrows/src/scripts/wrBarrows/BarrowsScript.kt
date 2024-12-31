package scripts.wrBarrows

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import org.tribot.script.sdk.types.Area
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.calculators.ResourceCounter
import scripts.utils.failsafes.RepetitiveActionManager
import scripts.utils.mouse.MousePainter
import scripts.wrBarrows.behaviors.barrowsTree
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.StateManager
import scripts.wrBarrows.overlay.OverlayPainter
import scripts.wrBarrows.player.BarrowsArea
import scripts.wrBarrows.rooms.RoomManager
import scripts.wrBarrows.tunnel.TunnelManager
import java.awt.Color
import java.awt.Graphics

@TribotScriptManifest(
    name = "WrBarrows",
    description = "Handles the Barrows Activity",
    category = "Combat",
    author = "WrEcked"
)
class BarrowsScript : TribotScript {

    private val logger: Logger
        get() {
            return Logger("WrBlastFurnace Lite")
        }

    private val managers: Container = registerManagers(this.logger)

    private fun registerManagers(logger: Logger): Container {
        return Container(
            RepetitiveActionManager(logger),
            RoomManager(logger),
            StateManager(logger),
            TunnelManager(logger)
        )
    }

    private fun setupHelpers() {
        Lottery.initLogger(this.logger)

        ResourceCounter.init(
            mapOf(
                "Trips" to null,
                "Barrow Items" to null,
            )
        )
    }

    private fun setup() {
        // Let's setup some UI stuff
        MousePainter().init()
        OverlayPainter(this.managers).init()

        // Move onto setting up helpers and what else we need to have prepared.
        this.setupHelpers()
    }

    override fun execute(args: String) {
        this.setup()

        this.paintAreas()

        this.executeBarrowsTree()
    }

    private fun executeBarrowsTree() {
        try {
            if (!Login.isLoggedIn()) {
                Waiting.waitUntil(5_000) {
                    Login.login()
                }
            }

            barrowsTree(
                logger = this.logger,
                managers = this.managers
            ).tick()

            logger.error("[Ending] - All tasks done")
        } catch (ex: Throwable) {
            handleExecutionError(logger, ex)
        } finally {
            safelyLogout(logger)
        }
    }

    private fun safelyLogout(logger: Logger) {
        //TODO handle in-combat preventing logouts
        // We should always do a teleport before

        val loggedOut = Waiting.waitUntil(30_000) {
            Login.logout()
        }

        if (!loggedOut) {
            logger.error("We failed to logout!")
        }
    }

    private fun handleExecutionError(logger: Logger, ex: Throwable) {
        logger.error("Error occurred! Message: ${ex.message}")
        ex.printStackTrace()
    }

    private fun paintAreas() {
        BarrowsArea.values().forEach {
            Logger("[Debug] - Painting ${it.name} area")
            paint(it.surface)
        }
    }

    private fun paint(area: Area) {
        Painting.addPaint { g: Graphics ->
            g.color = Color.blue
            val boundsToDraw = area.bounds

            g.drawPolygon(boundsToDraw)

            g.color = Color.BLUE.brighter()
            g.fillPolygon(boundsToDraw)
        }
    }
}