package scripts.wrBlastFurnace.behaviours.setup.actions

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.setup.validation.MoveToFurnaceValidation

/**
 * This node is designed to move our player towards the Blast Furnace
 * At first, we should start by supporting moving from the G.E.
 * so, this node is to be called upon when we're within the G.E. area
 * and we should move towards the mine cart trapdoor and interact on it.
 * await until we're at the Keldagrim location and from there move towards the furnace.
 * TODO, current set-up works, but does not properly adjust the camera, when walking.
 */
fun IParentNode.moveToFurnaceNode(logger: Logger) = sequence {
    //if we're at the G.E.
    // move to the minecart tile
    // interact with the trapdoor

    // since we will call this only when within the G.E area, initiate moving until we're within the keldagrim region

    selector {
        condition { !MoveToFurnaceValidation(logger).isNearTrapdoor() || !MoveToFurnaceValidation(logger).isWithinKeldagrim() }
        condition {
//            logger.info("Navigating to trapdoor")
            Waiting.waitUntil {
                GlobalWalking.walkTo(MoveToFurnaceValidation(logger).randomTrapdoorTile())
                MoveToFurnaceValidation(logger).isNearTrapdoor()
            }
        }
    }

    // Only if not in keldagrim but are near the trapdoor
    selector {
        condition { MoveToFurnaceValidation(logger).isWithinKeldagrim() && MoveToFurnaceValidation(logger).isNearTrapdoor() }
        condition {
            val trapdoor = Query.gameObjects()
                .idEquals(16168)
                .findBestInteractable()
                .map {
                    it.interact("Travel")
                }
                .orElse(false)

//            logger.debug("Trapdoor: ${trapdoor}")
            if (trapdoor) {
//                logger.debug("Trapdoor: ${trapdoor}")
//                logger.info("Traveling to Keldagrim")
                Waiting.waitUntil {
                    MoveToFurnaceValidation(logger).isWithinKeldagrim()
                }
                Waiting.wait(5000)
            }

            trapdoor
        }
    }

    selector {
        condition { MoveToFurnaceValidation(logger).isWithinKeldagrim() && MoveToFurnaceValidation(logger).isNearBlastFurnaceEntrance() }
        condition {
//            logger.info("Navigating to Blast Furnace Stairs")
            Waiting.waitUntil {
//                logger.info("We are going down!")
                GlobalWalking.walkTo(MoveToFurnaceValidation(logger).entranceStairsTile)
            }
        }
    }

    selector {
        condition { !MoveToFurnaceValidation(logger).isNearBlastFurnaceEntrance()}
        condition {
            logger.info("Standing besides the stairs, let's head on down.")

            Waiting.waitUntil {
                val climbDown = Query.gameObjects()
                    .idEquals(9084)
                    .isReachable()
                    .findBestInteractable()
                    .map { it.interact("Climb-down") }
                    .orElse(false)

                logger.debug("Climbing down state: ${climbDown}")
                climbDown
            }
        }
    }

}