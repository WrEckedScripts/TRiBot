package scripts.wrMotherlode.behaviours.motherlode.actions

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.FatiqueResolver
import kotlin.random.Random

fun IParentNode.walkToVeinsNode(logger: Logger) = sequence {
    selector {
        condition {
            val veinTile = WorldTile(
                3742 + Random.nextInt(4),
                5647 - Random.nextInt(4),
                0
            )

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