package scripts.wrBarrows.rooms

import scripts.utils.Logger

/**
 * Utility class to keep track of the crypts we've handled.
 */
class RoomManager(val logger: Logger) {
    var initialized: Boolean = false

    private var cryptCollection: MutableMap<String, RoomState> = mutableMapOf()

    fun init() {
        this.cryptCollection.clear()

        // This should directly go according to the order we want to.
        Room.values().forEach {
            this.cryptCollection[it.name] = RoomState(it, false, false)
        }

        Room.values().forEach {
            val room = this.cryptCollection[it.name]?.room
            if (null == room) {
                return@forEach
            }

            if (room.brother.varbit.get() == 1) {
                this.markAsCompleted(room)
            }
        }

        this.initialized = true
    }

    fun refresh() {
        this.cryptCollection.forEach {
            it.value.isCompleted = false
            it.value.isTunnel = false
        }
    }

    fun markAsCompleted(room: Room) {
        Logger("RoomManager").debug("Marking ${room.name} as completed")
        this.cryptCollection[room.name]!!.isCompleted = true
    }

    fun markAsTunnel(room: Room) {
        Logger("RoomManager").debug("Marking ${room.name} as tunnel")
        this.cryptCollection[room.name]!!.isTunnel = true
        this.markAsCompleted(room)
    }

    /**
     * Gets the first incomplete crypt to handle.
     */
    fun getTargetCrypt(): RoomState {
        val targetCrypt = this.cryptCollection.values.firstOrNull { !it.isCompleted }

        // If we have no more crypts left, we need to go to the tunnel!
        if (targetCrypt == null) {
            return this.cryptCollection.values.first { it.isTunnel }
        }

        return targetCrypt
    }

    /**
     * Returns the full Crypt State of which our player is inside of
     */
    fun getCurrentCrypt(): MutableMap.MutableEntry<String, RoomState>? {
        //TODO keep track of area inside the crypts
        // return which one our player is inside of.
        return this.cryptCollection.entries.find {
            it.value.room.area.crypt.containsMyPlayer()
        }
    }

    fun getRemainingCryptsCount(): Int {
        return this.cryptCollection.values.count { !it.isCompleted }.or(0)
    }

    fun shouldEnterTunnel(): Boolean {
        //Only execute when inside a crypt
        val areInsideTunnelCrypt = this.getCurrentCrypt()?.value?.isTunnel ?: false
        val trueRemainingCount = this.cryptCollection.values.count { !it.isTunnel && !it.isCompleted }

        Logger("RoomManager").error(trueRemainingCount)
        return trueRemainingCount <= 1 && areInsideTunnelCrypt
    }
}