package scripts.wrBlastFurnace.overlay

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintLocation
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.formatters.Countdown
import scripts.utils.formatters.Notator
import scripts.wrBlastFurnace.gui.Settings
import scripts.wrBlastFurnace.managers.Container
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
                .label("Stage")
                .value {
                    managers.tripStateManager.getCurrentKey()
                }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("Trips")
                .value {
                    managers.progressionManager.tripsLabel()
                }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("Bars")
                .value {
                    managers.progressionManager.barsLabel()
                }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("Gross earned")
                .value {
                    managers.progressionManager.grossProfit()
                }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("Spent")
                .value { managers.progressionManager.currentSpent() }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("Net earned")
                .value { managers.progressionManager.netProfit() }
                .build()
        )

        val sidePaint = BasicPaintTemplate.builder()
            .location(PaintLocation.TOP_RIGHT_VIEWPORT)
            .row(
                paintTemplate.toBuilder()
                    .label("Total upkeep spent")
                    .value { Notator.format(managers.upkeepManager.totalSpent) }
                    .build()
            )

        if (managers.upkeepManager.shouldPayForeman()) {
            sidePaint
                .row(
                    paintTemplate.toBuilder()
                        .label("Last foreman payment")
                        .value {
                            Countdown().fromMillis(
                                managers.upkeepManager.lastPaidForemanAt ?: System.currentTimeMillis()
                            )
                        }
                        .build()
                )
        }

        sidePaint
            .row(
                paintTemplate.toBuilder()
                    .label("Use Stamina's")
                    .value { if (Settings.staminaChecked) "Enabled" else "Disabled" }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Coal bag")
                    .value { if (Settings.coalBagChecked) "Enabled" else "Disabled" }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Pre-walk")
                    .value { if (Settings.preWalkChecked) "Enabled" else "Disabled" }
                    .build()
            )

        Painting.addPaint { mainPaint.build().render(it) }
        Painting.addPaint { sidePaint.build().render(it) }
    }
}
