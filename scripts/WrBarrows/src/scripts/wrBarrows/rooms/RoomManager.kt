package scripts.wrBarrows.rooms

import scripts.utils.Logger
import scripts.wrBarrows.player.BarrowsArea
import scripts.wrBarrows.player.BarrowsBrother

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
            this.cryptCollection[it.name] = RoomState(it, it.brother.varbit.get() == 1, false)
        }

        Room.values().forEach {
            val room = this.cryptCollection[it.name]?.room
            if (null == room) {
                return@forEach
            }
        }

        this.initialized = true
    }

    fun updateHandledRooms() {
        this.cryptCollection.forEach {
            if (it.value.room.brother.varbit.get() == 1) {
                this.markAsCompleted(it.value.room)
            }
        }
    }

    fun refresh() {
        this.cryptCollection.forEach {
            it.value.isCompleted = false
            it.value.isTunnel = false
            Logger("RoomManager - Refreshed").warn("${it.key} has been refresh!")
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
        val areInsideTunnelCrypt = BarrowsArea.BARROWS.crypt.containsMyPlayer()
        val trueRemainingCount = this.cryptCollection.values.count {
            Logger("Counting").warn("${it.room.brother.name} | !T:${!it.isTunnel} && !C:${!it.isCompleted}")
            !it.isTunnel && !it.isCompleted
        }

        Logger("RoomManager").error(trueRemainingCount)
        return trueRemainingCount == 0 && !areInsideTunnelCrypt
    }

    fun getRemainingBrother(): BarrowsBrother? {
        val tracked = this.cryptCollection.entries.filter {
            it.value.isTunnel
        }.firstOrNull()

        if (null != tracked) {
            return tracked.value.room.brother
        }

        return BarrowsBrother.values().firstOrNull {
            it.varbit.get() == 0
        }
    }
}