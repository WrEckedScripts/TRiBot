package scripts.wrMotherlode.behaviours.motherlode.actions

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.FatiqueResolver
import java.awt.Color
import java.awt.Graphics
import kotlin.random.Random

fun IParentNode.walkToVeinsNode(logger: Logger) = sequence {
    selector {
        condition {
            val veinTile = WorldTile(
                3750 + Random.nextInt(6),
                5650 + Random.nextInt(3),
                0
            )

            //TODO remove
            Painting.addPaint { g: Graphics ->
                g.color = Color.blue
                val boundsToDraw = veinTile.bounds

                if (boundsToDraw.isPresent) {
                    g.drawPolygon(boundsToDraw.get());
                }
            }

            if (veinTile.isVisible || veinTile.isOnMinimap) {
                Waiting.waitUntil(15_000 + FatiqueResolver.getMilliseconds()) {
                    !MyPlayer.isMoving()
                }

                return@condition true
            }

            Waiting.waitUntil {
                MyPlayer.getTile() != veinTile.tile
                LocalWalking.walkTo(veinTile)
            }
        }
    }
}