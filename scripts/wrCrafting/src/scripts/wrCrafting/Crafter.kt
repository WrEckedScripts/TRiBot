package scripts.wrCrafting

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Login
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate
import org.tribot.script.sdk.painting.template.basic.PaintRows
import org.tribot.script.sdk.painting.template.basic.PaintTextRow
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.script.ScriptRuntimeInfo
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger
import java.awt.Color
import java.awt.Font
import java.awt.Graphics

@TribotScriptManifest(
    name = "wrCrafting 0.0.3",
    description = "Auto Crafter",
    category = "Crafting",
    author = "WrEcked",
)
class Crafter : TribotScript {
    override fun execute(args: String) {
        val logger = Logger(ScriptRuntimeInfo.getScriptName())
        logger.debug("Started")

        initPaint()
        logger.debug("Painted!")

        val grandExchangeCenterTile = WorldTile(3167, 3488, 0);
        val radius = 3
        val grandExchangeArea = Area.fromRadius(grandExchangeCenterTile, radius)

        val logisticsManager = LogisticsManager(logger, grandExchangeArea)

        paintTile(grandExchangeCenterTile)
        logger.debug("Painted location-tile")

        val behaviorTree = getCraftingTrees(
            logisticsManager = logisticsManager,
            logger = logger
        )

        val tick = behaviorTree.tick()
        logger.debug("Behavior Tree TICK result: $tick");
    }

    private fun getCraftingTrees(logisticsManager: LogisticsManager, logger: Logger) = behaviorTree {
        repeatUntil(BehaviorTreeStatus.KILL) {
            logger.debug("repeatUntill KILL status given:")
            selector {
                sequence {
                    condition { !Login.isLoggedIn() }
                    perform { logger.debug("Waiting until logged in") }
                    perform { Login.login() }
                }

                sequence {
                    condition { !logisticsManager.inSkillingArea() }
                    perform { logger.debug("Walking to skilling area") }
                    perform { GlobalWalking.walkTo(logisticsManager.skillingArea.center) }
                }

                sequence {
                    condition {
                        val chiselPresent = Query.inventory()
                            .nameContains("Chisel")
                            .isNotNoted //this should've fixed the always false loop... (was isNoted, dumbass....)
                            .findRandom()
                            .isPresent

                        !chiselPresent
                    }
                    perform { logger.debug("Going to grab a Chisel from the bank")}
                    perform { Waiting.waitNormal(1000, 3000) }

                    selector {
                        condition { Bank.ensureOpen() }
                        sequence {
                            perform { GlobalWalking.walkToBank() }
                            perform { Bank.ensureOpen() }
                        }
                    }
                    perform {
                        logger.debug("banking time");
                        //todo this is should be a BankManager related flow:
                        Bank.depositInventory()

                        //todo if chisel isn't in bank, we should buy one
                        Bank.withdraw("Chisel", 1)
                        Bank.close()

                        Waiting.waitNormal(1000, 3000)
                        logger.debug("done sleeping")
                    }

                }
            }
        }
    }

    private fun paintTile(tile: WorldTile) {
        Painting.addPaint { g: Graphics ->
            g.color = Color.green
            val boundsToDraw = tile.bounds
            val polygon = boundsToDraw.get()

            if (boundsToDraw.isPresent) {
                g.drawPolygon(polygon)
            }
        }
    }

    private fun initPaint() {
        val paintTemplate = PaintTextRow.builder()
            .background(Color(62, 62, 62))
            .font(Font("Segoe UI", 0, 12))
            .build()

        val mainPaint = BasicPaintTemplate.builder()
            .row(PaintRows.scriptName(paintTemplate.toBuilder()))
            .row(PaintRows.runtime(paintTemplate.toBuilder()))
            .row(
                paintTemplate.toBuilder()
                    .label("Items crafted")
                    .value { 0 }
                    .build()
            )
            .build()

        Painting.addPaint { mainPaint.render(it) }
    }

}