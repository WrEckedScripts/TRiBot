package scripts.utils.antiban

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.util.TribotRandom

object MiniBreak {
    private var leaveActive: Boolean = false

    fun isActive(): Boolean {
        return this.leaveActive
    }

    fun stateForPaint(): String {
        return when (this.leaveActive) {
            true -> "Active"
            false -> "Not Active"
        }
    }

    fun miniLeave() {
        val leaveForMillis = TribotRandom.normal(5000, 2340)
        this.leaveActive = true
        Mouse.leaveScreen()
        Waiting.wait(leaveForMillis)
        this.leaveActive = false
    }
}