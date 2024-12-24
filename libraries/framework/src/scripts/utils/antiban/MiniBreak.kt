package scripts.utils.antiban

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

object MiniBreak {
    private var active: Boolean = false

    fun stateForPaint(): String {
        return when (this.active) {
            true -> "Active"
            false -> "Not Active"
        }
    }

    /**
     * A sometimes quick, sometimes longer leave
     */
    fun leave() {
        val milliseconds = TribotRandom.normal(15_000, 2340)
        this.active = true

        Logger("[MiniBreak]").info("Leaving screen for ${milliseconds}ms")

        Mouse.leaveScreen()
        Waiting.wait(milliseconds)

        this.active = false
    }

    /**
     * A fatigue based leave of the screen, influenced by runtime / time of day
     */
    fun fatigueLeave() {
        val milliseconds = FatigueResolver.getMilliseconds()
        this.active = true

        Logger("[MiniBreak]").info("Leaving screen for ${milliseconds}ms")

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