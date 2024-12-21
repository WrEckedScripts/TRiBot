package scripts.wrFoundry

import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.utils.Logger
import scripts.utils.failsafes.RepetitiveActionManager
import scripts.utils.mouse.MousePainter
import scripts.wrFoundry.behaviours.getFoundryTree
import scripts.wrFoundry.managers.Container
import scripts.wrFoundry.overlay.OverlayPainter

@TribotScriptManifest(
    name = "WrFoundry 1.2.1",
    author = "WrEcked",
    category = "Smithing",
    description = "Handles the Foundry"
)
class FoundryScript : TribotScript {
    private val logger: Logger
        get() {
            return Logger("WrFoundry")
        }

    private val managers: Container = registerManagers(this.logger)

    private fun registerManagers(logger: Logger): Container {

        return Container(
            repetitiveActionManager = RepetitiveActionManager(logger),
        )
    }

    override fun execute(args: String) {
        MousePainter().init()
        OverlayPainter(this.logger, this.managers).init()
        this.logger.error("START")

        val tree = getFoundryTree(
            this.logger,
            this.managers
        )

        val tick = tree.tick();
        this.logger.debug("[ENDING] - Ticked $tick")
    }
}