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

fun IParentNode.walkToVeinsNode(logger: Logger) = sequence {
    selector {
        condition {
            // todo, a couple random tiles to aim for, randomly picked
            //  might need a veinManager for this to not re-calculate every condition check?
            val veinTile = WorldTile(3742, 5647, 0)

            if (veinTile.isVisible || veinTile.isOnMinimap) {
                Waiting.waitUntil(15_000) {
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