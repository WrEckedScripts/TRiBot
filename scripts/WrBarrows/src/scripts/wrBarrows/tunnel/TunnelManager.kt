package scripts.wrBarrows.tunnel

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import scripts.utils.Logger

class TunnelManager(val logger: Logger) {
    private var spawnedAt: Spawn? = setSpawn()
    private var doors: MutableList<GameObject> = mutableListOf()

    fun init() {
        this.setSpawn()
        this.setDoors()
    }

    fun setSpawn(): Spawn? {
        this.spawnedAt = null

        return Spawn.values().find {
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

            this.doors.add(doorObject)
        }
    }

    fun getDoors(): MutableList<GameObject> {
        return this.doors
    }
}