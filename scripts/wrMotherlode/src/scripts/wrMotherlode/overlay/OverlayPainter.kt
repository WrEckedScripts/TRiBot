package scripts.wrMotherlode.overlay

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintLocation
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.debug.LastActionTracker
import scripts.utils.formatters.Countdown
import scripts.wrMotherlode.managers.Container
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
                .label("Currently")
                .value {
                    managers.stateManager.getCurrentKey()
                }
                .build()
        ).row(
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

        val sidePaint = BasicPaintTemplate.builder()
            .location(PaintLocation.TOP_RIGHT_VIEWPORT)


        //todo does this work..?
        sidePaint
            .row(
                paintTemplate.toBuilder()
                    .label("Coal")
                    .value {
                        ResourceCounter.getPaintLabelFor("Coal")
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Gold")
                    .value {
                        ResourceCounter.getPaintLabelFor("Gold ore")
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Mithril")
                    .value {
                        ResourceCounter.getPaintLabelFor("Mithril ore")
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Adamantite")
                    .value {
                        ResourceCounter.getPaintLabelFor("Adamantite ore")
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Runite")
                    .value {
                        ResourceCounter.getPaintLabelFor("Runite ore")
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Golden nuggets")
                    .value { ResourceCounter.getResourceCount("Golden nugget") }
                    .build()
            )


        Painting.addPaint { mainPaint.build().render(it) }
        Painting.addPaint { sidePaint.build().render(it) }
    }
}
