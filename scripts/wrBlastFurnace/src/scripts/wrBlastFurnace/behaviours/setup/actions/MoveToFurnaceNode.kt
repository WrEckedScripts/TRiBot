package scripts.wrBlastFurnace.behaviours.setup.actions

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
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
 */
fun IParentNode.moveToFurnaceNode(logger: Logger) = sequence {
    selector {
        condition { !MoveToFurnaceValidation(logger).isNearTrapdoor() || !MoveToFurnaceValidation(logger).isWithinKeldagrim() }
        condition {
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

            if (trapdoor) {
                Waiting.waitUntil {
                    MoveToFurnaceValidation(logger).isWithinKeldagrim()
                }
                Waiting.waitNormal(4000, 130)
            }

            trapdoor
        }
    }

    selector {
        condition { MoveToFurnaceValidation(logger).isWithinKeldagrim() && MoveToFurnaceValidation(logger).isNearBlastFurnaceEntrance() }
        condition {
            Waiting.waitUntil {
                GlobalWalking.walkTo(MoveToFurnaceValidation(logger).entranceStairsTile)
            }
        }
    }

    selector {
        condition { !MoveToFurnaceValidation(logger).isNearBlastFurnaceEntrance()}
        condition {
            Waiting.waitUntil {
                Query.gameObjects()
                    .idEquals(9084)
                    .isReachable()
                    .findBestInteractable()
                    .map { it.interact("Climb-down") }
                    .orElse(false)
            }
        }
    }

}