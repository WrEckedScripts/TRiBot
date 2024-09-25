package scripts.utils.antiban

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.util.TribotRandom

object MiniBreak {
    private var active: Boolean = false

    fun stateForPaint(): String {
        return when (this.active) {
            true -> "Active"
            false -> "Not Active"
        }
    }

    fun leave() {
        val milliseconds = TribotRandom.normal(5000, 2340)
        this.active = true

        Mouse.leaveScreen()
        Waiting.wait(milliseconds)

        this.active = false
    }

    fun pause() {
        this.active = true

        val milliseconds = TribotRandom.normal(1524, 4302)
        Waiting.wait(milliseconds)

        this.active = false
    }
}