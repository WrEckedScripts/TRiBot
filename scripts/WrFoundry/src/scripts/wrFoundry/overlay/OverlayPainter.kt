package scripts.wrFoundry.overlay

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.Logger
import scripts.utils.formatters.CompactNotator
import scripts.wrFoundry.managers.Container
import scripts.wrFoundry.states.CurrentProcessingTask
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
                .label("Stage")
                .value {
                    CurrentProcessingTask.current?.displayName
                }
                .build()
        )
            .row(
                paintTemplate.toBuilder()
                    .label("Heat")
                    .value {
                        CurrentProcessingTask.currentHeat?.displayName
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Mithril bar")
                    .value {
                        ResourceCounter.getPaintLabelFor("Mithril bar")
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Adamantite bar")
                    .value {
                        ResourceCounter.getPaintLabelFor("Adamantite bar")
                    }
                    .build()
            ).row(
                paintTemplate.toBuilder()
                    .label("Preforms")
                    .value {
                        CompactNotator.format(ResourceCounter.getResourceCount("Preforms"))
                    }
                    .build()
            )

        Painting.addPaint { mainPaint.build().render(it) }
    }
}