package scripts.wrFoundry.states

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Widget
import scripts.utils.Logger
import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.enums.Stage
import kotlin.jvm.optionals.getOrNull

object CurrentProcessingTask {
    // Heat blocks
    private const val highId: Int = 4454
    private const val mediumId: Int = 4472
    private const val lowId: Int = 4490

    var current: Stage? = null
    var currentHeat: Heat? = null

    /**
     * Upon determination of the Stage we're in, this method only, additionally, matches the texture to a Stage Enum
     * - This ensures our Stage is always updated, with the currently, highlighted stage.
     */
    fun get(): Stage? {
        // Find our active state highlight widget, and use it to resolve what is highlighted
        val stageIndicator = this.getStageIndicator()

        // Resolve the highlighted stage block, from the UI
        val currentStage = this.resolveHighlightedStage(stageIndicator)

        if (currentStage == null) {
            return null
        }

        when (currentStage.textureId) {
            this.highId -> Stage.TRIP_HAMMER
            this.mediumId -> Stage.GRINDSTONE
            this.lowId -> Stage.POLISHING_WHEEL
            else -> {
                Logger("[ActiveState]").error("We finished/have no task.")
                null
            }
        }.also {
            this.current = it
            this.currentHeat = it?.heat
        }

        return this.current
    }

    /**
     * Grab, from the UI, the widget containing the Stage Indicator Arrow
     * The Widget will be used to compare its location on the UI.
     * This way, we can determine what stage our player is in.
     */
    private fun getStageIndicator(): Widget {
        return Query.widgets()
            .inRoots(754)
            .indexEquals(82)
            .findFirst()
            .get()
    }


    /**
     * Based on the passed through, Stage Indicator (Arrow) Widget result.
     * We can determine the current active stage and resolve its texture.
     * By resolving the texture, we can match the proper stage.
     */
    private fun resolveHighlightedStage(stageIndicator: Widget): Widget? {
        return Query.widgets()
            .inRoots(754) // Find within 754 tree
            .inIndexPath(75) // subquery on 75 child and subchilds
            .filter { widget ->
                // Filter down, to widgets, that are either | RED, GREEN or ORANGE state textures
                widget.textureId == 4454 || widget.textureId == 4490 || widget.textureId == 4472
            }
            .filter { filtered ->
                // Find out, between these texture variants, which one's bounds.X equals our highlighted one
                // + 4 is for the difference between border and actual widget.
                filtered.bounds.x == (stageIndicator.bounds.x + 4)
            }
            .findFirst()
            .getOrNull()
    }
}