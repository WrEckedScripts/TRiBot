package scripts.utils.antiban

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.progress.DiscordNotifier

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
        DiscordNotifier.send("[MiniBreak] - Breaking for ${leaveForMillis}ms BRB!")
        this.leaveActive = true
        Mouse.leaveScreen()
        Waiting.wait(leaveForMillis)
        this.leaveActive = false
    }
}