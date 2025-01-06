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
        ).row(
            paintTemplate.toBuilder()
                .label("Barrow Items")
                .value { ResourceCounter.getResourceCount("Barrow Items") }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("State")
                .value { this.managers.stateManager.getCurrentKey() }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("Combat State")
                .value { this.managers.combatManager.satisfiesInCombatState() }
                .build()
        ).row(
            paintTemplate.toBuilder()
                .label("Attack state")
                .value { this.managers.combatManager.satisfiesAttack() }
                .build()
        )
            .row(
                paintTemplate.toBuilder()
                    .label("Attacking")
                    .value { this.managers.combatManager.playerIsAttacking() }
                    .build()
            )
            .row(
                paintTemplate.toBuilder()
                    .label("Spawned brother")
                    .value { this.managers.combatManager.getTargetBrother()?.brotherName }
                    .build()
            )

        Painting.addPaint { mainPaint.build().render(it) }
    }
}