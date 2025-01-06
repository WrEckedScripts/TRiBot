package scripts.wrBarrows.tunnel

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.GameObject
import org.tribot.script.sdk.types.WorldTile
import scripts.utils.Logger
import java.awt.Color
import java.awt.Graphics

class TunnelManager(val logger: Logger) {
    private var spawnedAt: Spawn? = null
    private var doors: MutableList<GameObject> = mutableListOf()

    fun init() {
        logger.debug("Initialize TunnelManager")

        this.setSpawn()
        this.setDoors()

        logger.debug("- -$spawnedAt")
        logger.debug("- -$doors")
    }

    fun setSpawn() {
        this.spawnedAt = null
        this.spawnedAt = Spawn.values().find {
            Painting.addPaint { g: Graphics ->
                g.color = Color.YELLOW
                g.drawPolygon(it.area.bounds)
            }

            it.area.containsMyPlayer()
        }
    }

    fun setDoors() {
        this.doors.clear()

        this.spawnedAt!!.doors.forEach {
            val doorObject = Query.gameObjects()
                .nameEquals("Door")
                .inArea(it)
                .findBestInteractable()
                .get()

            Painting.addPaint { g: Graphics ->
                g.color = Color.ORANGE
                g.drawPolygon(it.bounds)
            }

            this.doors.add(doorObject)
        }
    }

    fun getDoors(): MutableList<GameObject> {
        return this.doors
    }

    fun unsetDoor(door: GameObject) {
        this.doors.removeIf { door.equals(it) }
    }

    fun missesSpawn(): Boolean {
        return this.spawnedAt == null
    }

    fun hasSpawn(): Boolean {
        return this.spawnedAt != null
    }

    fun insideChestRoom(): Boolean {
        val area = Area.fromPolygon(
            WorldTile(3546, 9702, 0),
            WorldTile(3545, 9689, 0),
            WorldTile(3558, 9689, 0),
            WorldTile(3558, 9700, 0)
        )

        return area.containsMyPlayer()
    }
}