package scripts.utils.antiban

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

object MiniBreak {
    private var active: Boolean = false

    /**
     * Leave based on mean/sd optional parameters
     */
    fun leave(mean: Int = 15_000, sd: Int = 2_000) {
        val milliseconds = TribotRandom.normal(mean, sd)
        this.active = true

        Logger("[MiniBreak]").info("Leaving screen for ${milliseconds}ms")

        Mouse.leaveScreen()
        Waiting.wait(milliseconds)

        this.active = false
    }

    /**
     * A fatigue based leave of the screen, influenced by runtime / time of day
     */
    fun fatigueLeave(multiplier: Int = 1) {
        val milliseconds = FatigueResolver.getMilliseconds() * multiplier
        this.active = true

        Logger("[MiniBreak]").info("Leaving screen for ${milliseconds}ms")

        Mouse.leaveScreen()
        Waiting.wait(milliseconds)

        this.active = false
    }

    /**
     * On screen pause, the mouse does NOT leave the screen, but simply wait until our next move
     */
    fun pause(mean: Int = 1_500, sd: Int = 4_300) {
        this.active = true

        val milliseconds = TribotRandom.normal(mean, sd)
        Waiting.wait(milliseconds)

        this.active = false
    }

    /**
     * A fatigue based pausing time, we do NOT leave the screen, but simply wait until our next move
     */
    fun fatiguePause(multiplier: Int = 1) {
        this.active = true

        val milliseconds = FatigueResolver.getMilliseconds() * multiplier
        Waiting.wait(milliseconds)

        this.active = false
    }
}