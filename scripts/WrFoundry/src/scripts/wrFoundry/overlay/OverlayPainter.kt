package scripts.wrFoundry.overlay

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.Logger
import scripts.wrFoundry.managers.Container
import scripts.wrFoundry.states.CurrentProcessingTask
import scripts.wrFoundry.states.HeatLevel
import java.awt.Color
import java.awt.Font

class OverlayPainter(
    private val logger: Logger,
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
                .label("Active State")
                .value {
                    CurrentProcessingTask.current?.displayName
                }
                .build()
        )
            .row(
                paintTemplate.toBuilder()
                    .label("Active Heat")
                    .value {
                        CurrentProcessingTask.currentHeat?.displayName
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Varbit match")
                    .value {
                        HeatLevel.get()?.displayName
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Varbit value")
                    .value {
                        HeatLevel.getRawValue().toString()
                    }
                    .build()
            )
//            .row(
//                paintTemplate.toBuilder()
//                    .label("Last Clicked")
//                    .value {
//                        Countdown().fromMillis(
//                            LastActionTracker.show("click")
//                        ).plus(" ago ")
//                    }
//                    .build()
//            ).row(
//                paintTemplate.toBuilder()
//                    .label("State since")
//                    .value {
//                        Countdown().fromMillis(
//                            LastActionTracker.show("state")
//                        )
//                    }
//                    .build()
//            )

        Painting.addPaint { mainPaint.build().render(it) }
    }
}