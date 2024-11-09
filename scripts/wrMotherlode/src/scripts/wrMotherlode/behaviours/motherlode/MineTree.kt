package scripts.wrMotherlode.behaviours.motherlode

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.debug.LastActionTracker
import scripts.wrMotherlode.banking.actions.ensureMineReadyInventory
import scripts.wrMotherlode.behaviours.motherlode.actions.fillHopperNode
import scripts.wrMotherlode.behaviours.motherlode.actions.mineVeinsNode
import scripts.wrMotherlode.behaviours.motherlode.actions.walkToVeinsNode
import scripts.wrMotherlode.managers.Container
import kotlin.jvm.optionals.getOrNull

fun getMineTree(
    logger: Logger,
    managers: Container
) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        sequence {
            /**
             * Ensures that we're logged in, after we get disconnected for example.
             */
            selector {
                condition { Login.isLoggedIn() }
                condition {
                    logger.debug("login condition")
                    Login.login()
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("REPAIRING") }
                sequence {
                    condition {
                        val brokenStrut = Query.gameObjects()
                            .actionContains("Hammer")
                            .findBestInteractable()
                            .getOrNull()

                        if (null == brokenStrut) {
                            logger.info("No broken struts, continue-ing!")

                            return@condition true
                        }

                        if (brokenStrut.tile.isOnMinimap || brokenStrut.tile.isVisible) {
                            Waiting.waitUntil {
                                brokenStrut.interact("Hammer")

                                LastActionTracker.track("click")

                                Waiting.waitNormal(3_000, 245)

                                Query.gameObjects()
                                    .actionContains("Hammer")
                                    .findBestInteractable()
                                    .isEmpty
                            }
                        } else {
                            Waiting.waitUntil {
                                LocalWalking.walkTo(brokenStrut.tile)

                                MyPlayer.getTile() != brokenStrut.tile
                            }
                        }
                    }
                    condition {
                        logger.debug("moving to next state after repairing....");
                        managers.stateManager.moveToNextState()
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("COLLECTING") }
                sequence {
                    condition {
                        // BEGIN: walkToSackNode
                        val oreSackTile = WorldTile(3749, 5659, 0)

                        if (!oreSackTile.isOnMinimap || !oreSackTile.isVisible) {
                            Waiting.waitUntil {
                                logger.debug("Walking to oresack")
                                LocalWalking.walkTo(oreSackTile)

                                MyPlayer.getTile() != oreSackTile.tile
                            }
                        }

                        // END

                        // BEGIN: collectFromSackNode
                        Waiting.waitUntil {
                            Query.gameObjects()
                                .nameEquals("Sack") //or something?
                                .findBestInteractable()
                                .getOrNull()?.interact("Search") //Or collect??

                            LastActionTracker.track("click")

                            Waiting.waitNormal(3_500, 1_123)
                            Inventory.getFilledSlots() > 2 // any better solution?
                        }

                        // We have collected, thus let's register the loot!
                        managers.progressionManager.registerLoot()

                        //END

                        // BEGIN: bankLootNode
                        val bankTask = BankTask.builder()
                            .addInvItem(1275, Amount.of(1)) // Rune Pickaxe
                            .addInvItem(2347, Amount.of(1)) // Hammer
                            .build()

                        Waiting.waitUntil {
                            if (!bankTask.isSatisfied() || Inventory.getFilledSlots() > 2) {
                                bankTask.execute()
                            }

                            bankTask.isSatisfied()
                        }
                        //END

                        // Check if there's any more to be looted
                        // If the remaining space, is not between 1..108 it's false (inversed to true)
                        // and we're done looting :)
                        // todo, needs changing, if we go for varbit.
//                        if (managers.sackManager.getRemainingSpace() !in 1..108) {
                        if (!managers.sackManager.needsFilling()) {
                            logger.warn("We got more loot to grab!")
                            return@condition false
                        }

                        logger.info("Done looting")
                        managers.stateManager.moveToNextState()
                        return@condition true
                    }
                }
            }

            selector {
                condition { managers.sackManager.needsFilling() }
                sequence {
                    condition {
                        logger.info("resetting state to collect")
                        managers.stateManager.resetCycle("COLLECTING")
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("FILLING") }
                sequence {
                    fillHopperNode(logger, managers)
                    selector {
                        condition { !managers.sackManager.needsFilling() }
                        condition {
                            logger.debug("Emptying inventory of any pay-dirt")
                            Waiting.waitUntil {
                                Query.inventory()
                                    .nameEquals("Pay-dirt")
                                    .forEach {
                                        it.click("Drop")
                                    }

                                !Inventory.contains("Pay-dirt")
                            }
                        }
                    }
                    condition {
                        managers.stateManager.moveToNextState()
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("MINING") }
                sequence {
                    ensureMineReadyInventory(logger, managers)
                    walkToVeinsNode(logger)
                    mineVeinsNode(logger, managers)
                    condition {
                        logger.debug("Moving ${managers.stateManager.getCurrentKey()} to the next.")
                        managers.stateManager.moveToNextState()
                    }
                }
            }

        }
    }
}