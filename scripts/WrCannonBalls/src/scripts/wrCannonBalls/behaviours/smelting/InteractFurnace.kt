package scripts.wrCannonBalls.behaviours.smelting

import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.antiban.MiniBreak
import scripts.utils.debug.LastActionTracker
import scripts.wrCannonBalls.managers.Container

class InteractFurnace(
    val logger: Logger,
    val managers: Container
) {
    fun execute(): Boolean {
        logger.warn("INTERACTFURNACETASK")
        LastActionTracker.track("state")

        if (MakeScreen.isOpen()) {
            logger.warn("MakeScreen is already open")
            MakeScreen.makeAll("Cannonball")

            return true
        }

        val furnace = Query.gameObjects()
            .nameEquals("Furnace")
            .findBestInteractable()

        if (furnace.isPresent) {
            this.logger.warn("Found furnace, using 'Ammo mould' on it.")
            furnace.get().interact("Smelt")
            LastActionTracker.track("click")

            val result = Waiting.waitUntil {
                MakeScreen.isOpen()

                Waiting.wait(FatigueResolver.getMilliseconds())

                MakeScreen.makeAll("Cannonball")
            }

            MiniBreak.leave()

            this.logger.debug("Interacted resulted in ${result}")

            return result
        }

        return false
    }
}