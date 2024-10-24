package scripts.wrBlastFurnace.behaviours.furnace.failsafes

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.WorldTile
import scripts.utils.Logger
import scripts.utils.failsafes.RepetitiveActionManager
import scripts.wrBlastFurnace.managers.Container

object SmithingArea {
    private var logger: Logger? = null
    private var repetitiveActionManager: RepetitiveActionManager? = null

    fun initLogger(log: Logger) {
        this.logger = log
    }

    fun registerManager(container: Container) {
        this.repetitiveActionManager = container.repetitiveActionManager
    }

    /**
     * Contains the bottom-left and top-right positions of the smithing area within the Blast Furnace
     */
    private val areaBounds = listOf(
        WorldTile(1935, 4970, 0), //bottom-left
        WorldTile(1943, 4974, 0), //top-right
    )

    fun isInsideArea(): Boolean {
        return MyPlayer.getTile().x in areaBounds[0].x..areaBounds[1].x
                && MyPlayer.getTile().y in areaBounds[0].y..areaBounds[1].y
    }

    fun handleEscape(): Boolean {
        this.logger?.error("[Failsafe] - Escaping smithing area")
        this.repetitiveActionManager!!.increment("smithing-area")

        Query.gameObjects()
            .actionEquals("Open")
            .findBestInteractable()
            .map { gate -> gate.interact("Open") }

        val escaped = Waiting.waitUntil(15_000) {
            Waiting.waitNormal(2_000, 540)
            !this.isInsideArea()
        }

        if (!escaped) {
            this.logger?.error("[Escape] - Failed to escape the Smithing area..")
            return false
        }

        this.repetitiveActionManager!!.reset("smithing-area")
        return true
    }
}