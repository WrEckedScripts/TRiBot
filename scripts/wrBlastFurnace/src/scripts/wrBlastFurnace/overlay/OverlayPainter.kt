package scripts.wrBlastFurnace.overlay

import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintLocation
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import scripts.utils.formatters.Coins
import scripts.utils.formatters.Countdown
import scripts.wrBlastFurnace.managers.PlayerRunManager
import scripts.wrBlastFurnace.managers.ProgressionManager
import scripts.wrBlastFurnace.managers.StaminaManager
import scripts.wrBlastFurnace.managers.UpkeepManager
import java.awt.Color
import java.awt.Font

class OverlayPainter(
    private val progressionManager: ProgressionManager,
    private val upkeepManager: UpkeepManager,
    private val staminaManager: StaminaManager,
    private val playerRunManager: PlayerRunManager
) {
    fun init() {
        val paintTemplate = PaintTextRow.builder()
            .background(Color(62, 62, 62))
            .font(Font("Segoe UI", 0, 12))
            .build()

        val mainPaint = BasicPaintTemplate.builder()
            .row(PaintRows.scriptName(paintTemplate.toBuilder()))
            .row(PaintRows.runtime(paintTemplate.toBuilder()))
            .row(
                paintTemplate.toBuilder()
                    .label("Handle Secondary")
                    .value { progressionManager.indicateState("PROCESS_SECONDARY") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Handle Base")
                    .value { progressionManager.indicateState("PROCESS_BASE") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Collect Bars")
                    .value { progressionManager.indicateState("COLLECT_BARS") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Bank Bars")
                    .value { progressionManager.indicateState("BANK_BARS") }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Trips")
                    .value {
                        progressionManager.currentTrips()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Forecast")
                    .value {
                        progressionManager.estimatedPerHourTrips()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Gross earned")
                    .value {
                        progressionManager.grossProfit()
                    }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Spent")
                    .value { progressionManager.currentSpent() }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Net earned")
                    .value { progressionManager.netProfit() }
                    .build()
            )

        val sidePaint = BasicPaintTemplate.builder()
            .location(PaintLocation.TOP_RIGHT_VIEWPORT)
            .row(
                paintTemplate.toBuilder()
                    .label("Total upkeep spent")
                    .value { Coins().format(upkeepManager.totalSpent) }
                    .build()
            )

        if (upkeepManager.shouldPayForeman()) {
            sidePaint
                .row(
                    paintTemplate.toBuilder()
                        .label("Last foreman payment")
                        .value { Countdown().fromMillis(upkeepManager.lastPaidForemanAt ?: System.currentTimeMillis()) }
                        .build()
                )
        }

        sidePaint
            .row(
                paintTemplate.toBuilder()
                    .label("Sip Stamina")
                    .value { if (staminaManager.satisfiesStaminaState()) "No" else "Yes" }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Re-enable run at")
                    .value(playerRunManager.getNextEnableAtValue().toString().plus("%"))
                    .build()
            )

        Painting.addPaint { mainPaint.build().render(it) }
        Painting.addPaint { sidePaint.build().render(it) }
    }
}
