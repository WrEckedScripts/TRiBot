package scripts.wrBarrows.rooms

import scripts.utils.Logger

/**
 * Utility class to keep track of the crypts we've handled.
 */
class RoomManager(val logger: Logger) {
    private var cryptCollection: MutableMap<String, RoomState> = mutableMapOf()

    fun init() {
        this.cryptCollection.clear()

        // This should directly go according to the order we want to.
        Room.values().forEach {
            this.cryptCollection[it.name] = RoomState(it, false, false)
        }
    }

    fun refresh() {
        this.cryptCollection.forEach {
            it.value.isCompleted = false
            it.value.isTunnel = false
        }
    }

    fun markAsCompleted(room: Room) {
        this.cryptCollection[room.name]!!.isCompleted = true
    }

    fun markAsTunnel(room: Room) {
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
        return this.getRemainingCryptsCount() <= 1
    }
}