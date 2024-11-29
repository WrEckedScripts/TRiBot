package scripts.wrFoundry

import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.utils.Logger
import scripts.wrFoundry.managers.Container

@TribotScriptManifest(
    name = "WrFoundry 1.0.0",
    author = "WrEcked",
    category = "Smithing",
    description = "Handles the Foundry"
)
class FoundryScript : TribotScript {
    private val logger: Logger
        get() {
            return Logger("WrCannonBalls")
        }

    private val managers: Container = registerManagers(this.logger)

    private fun registerManagers(logger: Logger): Container {
        TODO("Not yet implemented")
    }

    override fun execute(args: String) {

    }
}