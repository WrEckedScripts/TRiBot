package scripts.wrBlastFurnace.behaviours.setup.validation

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class MoveToFurnaceValidation(val logger: Logger) {
    private val trapDoorSurroundingTiles = listOf(
        WorldTile(3140, 3503, 0),
        WorldTile(3140, 3505, 0),
        WorldTile(3141, 3504, 0),
    )

    val entranceStairsTile = WorldTile(2931, 10196,0)

    fun randomTrapdoorTile(): WorldTile {
        val pick = TribotRandom.uniform(0, trapDoorSurroundingTiles.size - 1)
        val picked = trapDoorSurroundingTiles.get(pick)
        logger.debug("Picked tile: ${picked}")
        return picked
    }

    fun isNearTrapdoor(): Boolean {
        val trapdoorTile = WorldTile(3141, 3504, 0)
        val radius = 4
        val trapdoorArea = Area.fromRadius(trapdoorTile, radius)

        logger.debug("isNearTrapdoor ${trapdoorArea.containsMyPlayer()}")
        return trapdoorArea.containsMyPlayer()
    }

    fun isWithinKeldagrim(): Boolean {
        val keldagrimTile = WorldTile(2909, 10174, 0)
        val radius = 1
        val keldagrimArea = Area.fromRadius(keldagrimTile, radius)

        logger.debug("isWithinKeldagrim ${keldagrimArea.containsMyPlayer()}")
        Waiting.wait(3000) // this check is triggered way to fast, due to usage in conditions
        return keldagrimArea.containsMyPlayer()
    }

    fun isNearBlastFurnaceEntrance(): Boolean {
        val entranceArea = Area.fromRadius(this.entranceStairsTile, 1)

        logger.debug("isNearBFEntrance ${entranceArea.containsMyPlayer()}")
        return entranceArea.containsMyPlayer()
    }
}