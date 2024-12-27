package scripts.wrCannonBalls.overlay

import org.tribot.script.sdk.Skill
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintLocation
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.calculators.ResourceCounter
import scripts.utils.calculators.skills.TrackerCollection
import scripts.utils.failsafes.LastActionTracker
import scripts.utils.formatters.CompactNotator
import scripts.utils.formatters.Countdown
import scripts.wrCannonBalls.managers.Container
import java.awt.Color
import java.awt.Font

class OverlayPainter(
    private val managers: Container
) {
    fun init() {
        val paintTemplate = PaintTextRow.builder()
            .background(Color(62, 62, 62))
            .font(Font("Segoe UI", 0, 12))
            .build()

        val mainPaint = BasicPaintTemplate.builder()
            .row(PaintRows.scriptName(paintTemplate.toBuilder()))
            .row(PaintRows.runtime(paintTemplate.toBuilder()))

        mainPaint.row(
            paintTemplate.toBuilder()
                .label("Last Clicked")
                .value {
                    Countdown().fromMillis(
                        LastActionTracker.show("click")
                    ).plus(" ago ")
                }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("In state for")
                .value {
                    Countdown().fromMillis(
                        LastActionTracker.show("state")
                    )
                }
                .build()
        )

        Painting.addPaint { mainPaint.build().render(it) }

        val sidePaint = BasicPaintTemplate.builder()
            .location(PaintLocation.TOP_RIGHT_VIEWPORT)

        sidePaint
            .row(
                paintTemplate.toBuilder()
                    .label("Cannonballs")
                    .value {
                        ResourceCounter.getPaintLabelFor("Cannonball")
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Bars used")
                    .value {
                        ResourceCounter.getPaintLabelFor("Steel bar")
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Trips")
                    .value { CompactNotator.format(ResourceCounter.getResourceCount("Trips")) }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("EXP")
                    .value { TrackerCollection.get(Skill.SMITHING.name)!!.expLabel() }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Lvl")
                    .value { TrackerCollection.get(Skill.SMITHING.name)!!.levelLabel() }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("TTL")
                    .value { TrackerCollection.get(Skill.SMITHING.name)!!.timeToNextLevelLabel() }
                    .build()
            )

        Painting.addPaint { sidePaint.build().render(it) }
    }
}
