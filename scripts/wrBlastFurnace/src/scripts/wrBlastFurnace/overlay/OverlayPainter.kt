package scripts.wrBlastFurnace.overlay

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintLocation
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.formatters.Coins
import scripts.utils.formatters.Countdown
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

        if (managers.tripStateManager.secondaryOre != null) {
            mainPaint.row(
                paintTemplate.toBuilder()
                    .label("Handle Secondary")
                    .value { managers.progressionManager.indicateState("PROCESS_SECONDARY") }
                    .build()
            )
        }

        mainPaint.row(
            paintTemplate.toBuilder()
                .label("Handle Base")
                .value { managers.progressionManager.indicateState("PROCESS_BASE") }
                .build()
        )
            .row(
                paintTemplate.toBuilder()
                    .label("Collect Bars")
                    .value { managers.progressionManager.indicateState("COLLECT_BARS") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Bank Bars")
                    .value { managers.progressionManager.indicateState("BANK_BARS") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Trips")
                    .value {
                        managers.progressionManager.currentTrips()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Forecast")
                    .value {
                        managers.progressionManager.estimatedPerHourTrips()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Gross earned")
                    .value {
                        managers.progressionManager.grossProfit()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Spent")
                    .value { managers.progressionManager.currentSpent() }
                    .build()
            )
            .row(
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
                    .value { Coins().format(managers.upkeepManager.totalSpent) }
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
                    .label("Sip Stamina")
                    .value { if (Settings.staminaChecked) "Yes" else "No" }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Re-enable run at")
                    .value(managers.playerRunManager.getNextEnableAtValue().toString().plus("%"))
                    .build()
            )

        Painting.addPaint { mainPaint.build().render(it) }
        Painting.addPaint { sidePaint.build().render(it) }
    }
}
