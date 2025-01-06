package scripts.wrMotherlode.behaviours.motherlode.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.input.Mouse
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.antiban.Lottery
import scripts.utils.calculators.ResourceCounter
import scripts.utils.failsafes.LastActionTracker
import scripts.wrMotherlode.managers.Container
import java.awt.Color
import java.awt.Graphics

fun IParentNode.mineVeinsNode(
    logger: Logger,
    managers: Container
) = sequence {
    condition {
        if (Inventory.getEmptySlots() == 0) {
            ResourceCounter.increment("Pay-dirt", Inventory.getCount("Pay-dirt"))
            managers.stateManager.moveToNextState()

            return@condition true
        }

        // Make sure our inventory is ready for mining veins
        // - We should've banked before, only holding a pickaxe and a hammer at max.
        var interacted = false

        val vein = Query.gameObjects()
            .nameEquals("Ore vein")
            .findBestInteractable()

        if (vein.isPresent) {
            interacted = vein
                .map { oreVein ->

                    //TODO remove
                    Painting.addPaint { g: Graphics ->
                        g.color = Color.green
                        val boundsToDraw = oreVein.tile.bounds

                        if (boundsToDraw.isPresent) {
                            g.drawPolygon(boundsToDraw.get())
                        }
                    }

                    oreVein.interact("Mine")
                }
                .orElse(false)

            Waiting.wait(FatigueResolver.getMilliseconds())
        }

        if (interacted) {
            LastActionTracker.track("click")

            Lottery.execute(0.9) {
                Mouse.leaveScreen()
            }

            // Give the script 3 seconds to start animating, otherwise we should fail
            if (Waiting.waitUntilAnimating(3_000 + FatigueResolver.getMilliseconds())) {
                // If we're animating, let's wait until we're not anymore.
                Waiting.waitUntil(35_000, 10_000 + FatigueResolver.getMilliseconds()) {
                    !MyPlayer.isAnimating()
                }
            }
        }

        if (Inventory.getCount("Pay-dirt") >= managers.sackManager.getRemainingSpace()) {
            ResourceCounter.increment("Pay-dirt", Inventory.getCount("Pay-dirt"))

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
        Waiting.wait(FatigueResolver.getMilliseconds())
        Inventory.getEmptySlots() == 0
    }
}