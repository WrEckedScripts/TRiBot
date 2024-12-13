package scripts.wrCannonBalls

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.utils.Logger
import scripts.utils.antiban.RuntimeTracker
import scripts.utils.debug.LastActionTracker
import scripts.utils.failsafes.RepetitiveActionManager
import scripts.utils.mouse.MousePainter
import scripts.wrCannonBalls.behaviours.getSmeltTree
import scripts.wrCannonBalls.managers.Container
import scripts.wrCannonBalls.overlay.OverlayPainter

@TribotScriptManifest(
    name = "WrCannonBalls",
    description = "Smelts anything but cannonballs...",
    category = "Smithing",
    author = "WrEcked"
)
class CannonBallsScript : TribotScript {
    private val logger: Logger
        get() {
            return Logger("WrCannonBalls")
        }

    private val managers: Container = registerManagers(this.logger)

    private fun registerManagers(logger: Logger): Container {
        val repetitiveActionManager = RepetitiveActionManager(logger)

        return Container(
            repetitiveActionManager
        )
    }

    private fun setupHelpers() {
        RuntimeTracker.init()
//        RuntimeTracker.initLogger(this.logger)

//        FatigueResolver.initLogger(this.logger)
        MousePainter().init()
        OverlayPainter(this.managers).init()
    }


    override fun execute(arg: String) {
        setupHelpers()

        try {
            if (!Login.isLoggedIn()) {
                Waiting.waitUntil {
                    Login.login()
                }
            }

            LastActionTracker.track("state")
            val smeltTree = getSmeltTree(logger, managers)
            val tick = smeltTree.tick()
            logger.error("TICK: ${tick}")

        } catch (ex: Throwable) {
            handleExceptions(logger, ex)
        } finally {
            safelyLogout(logger)
            throw Exception("Forceful logout exception to prevent TRiBot X from re-logging")
        }
    }

    private fun handleExceptions(logger: Logger, ex: Throwable) {
        logger.error(ex.message)
        ex.printStackTrace()
    }

    private fun safelyLogout(logger: Logger) {
        Waiting.waitUntil(15_000) {
            Login.logout()
        }

        logger.warn("Successfully logged out!")
    }
}