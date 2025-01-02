package scripts.wrBarrows.overlay

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.calculators.ResourceCounter
import scripts.wrBarrows.managers.Container
import java.awt.Color
import java.awt.Font

class OverlayPainter(val managers: Container) {
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
                .label("Trips")
                .value { ResourceCounter.getResourceCount("Trips") }
                .build()
        )

        mainPaint.row(
            paintTemplate.toBuilder()
                .label("Barrow Items")
                .value { ResourceCounter.getResourceCount("Barrow Items") }
                .build()
        )

        mainPaint.row(
            paintTemplate.toBuilder()
                .label("State")
                .value { this.managers.stateManager.getCurrentKey() }
                .build()
        )

        if (this.managers.roomManager.initialized) {
            mainPaint.row(
                paintTemplate.toBuilder()
                    .label("Target Crypt")
                    .value { this.managers.roomManager.getTargetCrypt().room.name }
                    .build()
            )
            mainPaint.row(
                paintTemplate.toBuilder()
                    .label("- completed")
                    .value { this.managers.roomManager.getCurrentCrypt()?.value?.isCompleted }
                    .build()
            )

            mainPaint.row(
                paintTemplate.toBuilder()
                    .label("- tunnel")
                    .value { this.managers.roomManager.getCurrentCrypt()?.value?.isTunnel }
                    .build()
            )
        }

        Painting.addPaint { mainPaint.build().render(it) }
    }
}