package scripts.wrMotherlode.behaviours.motherlode.actions

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.failsafes.LastActionTracker
import kotlin.jvm.optionals.getOrNull

fun IParentNode.repairBrokenStrutNode(logger: Logger) = condition {
    val brokenStrut = Query.gameObjects()
        .actionContains("Hammer")
        .findBestInteractable()
        .getOrNull()

    if (null == brokenStrut) {
        logger.info("No broken struts, continue-ing!")

        return@condition true
    }

    if (brokenStrut.tile.isOnMinimap || brokenStrut.tile.isVisible) {
        Waiting.waitUntil(20_000, 5_000 + FatigueResolver.getMilliseconds()) {
            brokenStrut.interact("Hammer")

            LastActionTracker.track("click")

            Waiting.wait(FatigueResolver.getMilliseconds())

            Query.gameObjects()
                .actionContains("Hammer")
                .findBestInteractable()
                .isEmpty
        }
    } else {
        Waiting.waitUntil(15_000, 2_000 + FatigueResolver.getMilliseconds()) {
            LocalWalking.walkTo(brokenStrut.tile)

            MyPlayer.getTile() != brokenStrut.tile
        }
    }
}