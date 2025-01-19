package scripts.wrMule

import org.tribot.script.sdk.Log

object WorkerTarget {
    var hasActiveTarget = false
    var targetName: String = ""

    fun reset() {
        this.hasActiveTarget = false
        this.targetName = ""
        Log.warn("Re-opened for a trade...")
    }

    fun target(name: String) {
        this.targetName = name
        this.hasActiveTarget = true
        Log.warn("Reserving next trade for new target: ${this.targetName}")
    }

}