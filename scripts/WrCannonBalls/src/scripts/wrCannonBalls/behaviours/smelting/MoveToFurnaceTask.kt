package scripts.wrCannonBalls.behaviours.smelting

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.debug.LastActionTracker
import scripts.wrCannonBalls.managers.Container

class MoveToFurnaceTask(
    val logger: Logger,
    val managers: Container
) {
    private val location: WorldTile = WorldTile(3107, 3498, 0)

    fun execute(): Boolean {
        logger.warn("MOVETOFURNACETASK")
        var interacted = false

        val furnace = Query.gameObjects()
            .nameEquals("Furnace")
            .findBestInteractable()

        if (furnace.isPresent) {
            this.logger.warn("Found furnace, moving to Smelt bars into balls")
            interacted = furnace.get().interact("Smelt")
            Waiting.wait(FatigueResolver.getMilliseconds() * 2)
        }

        if (interacted) {
            LastActionTracker.track("state")

            return interacted
        }

        LocalWalking.walkTo(location)

        return MyPlayer.getTile() == furnace.get().tile
    }
}