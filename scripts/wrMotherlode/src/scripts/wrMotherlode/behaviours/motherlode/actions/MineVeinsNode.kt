package scripts.wrMotherlode.behaviours.motherlode.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatiqueResolver
import scripts.utils.antiban.Lottery
import scripts.utils.debug.LastActionTracker
import scripts.wrMotherlode.managers.Container

fun IParentNode.mineVeinsNode(
    logger: Logger,
    managers: Container
) = sequence {
    condition {
        if (Inventory.getEmptySlots() == 0) {
            logger.debug("Our inventory is full! moving to next state.")

            managers.stateManager.moveToNextState()

            return@condition true
        }

        // Make sure our inventory is ready for mining veins
        // - We should've banked before, only holding a pickaxe and a hammer at max.
        var interacted = false

        val vein = Query.gameObjects()
            .nameEquals("Ore vein")
            .findClosestByPathDistance()

        if (vein.isPresent) {
            interacted = vein
                .map { oreVein -> oreVein.interact("Mine") }
                .orElse(false)

            Waiting.wait(FatiqueResolver.getMilliseconds())
        }

        if (interacted) {
            LastActionTracker.track("click")

            Lottery.execute(0.9) {
                Mouse.leaveScreen()
            }

            // Give the script 3 seconds to start animating, otherwise we should fail
            if (Waiting.waitUntilAnimating(3_000 + FatiqueResolver.getMilliseconds())) {
                // If we're animating, let's wait until we're not anymore.
                Waiting.waitUntil(35_000, 10_000 + FatiqueResolver.getMilliseconds()) {
                    !MyPlayer.isAnimating()
                }
            }
        }

        if (Inventory.getCount("Pay-dirt") >= managers.sackManager.getRemainingSpace()) {
            logger.debug("We mined enough, early stopping mining")

            managers.stateManager.moveToNextState()

            return@condition true
        }

        //TODO, can't we simple use the Inv.getEmptySlots as a final returning value?
        // seems like a unnecessary wait
//        Waiting.waitUntil(10_000) {
//            Waiting.waitNormal(1748, 256)
//
//            Inventory.getEmptySlots() == 0
//        }
        Waiting.wait(FatiqueResolver.getMilliseconds())
        Inventory.getEmptySlots() == 0
    }
}