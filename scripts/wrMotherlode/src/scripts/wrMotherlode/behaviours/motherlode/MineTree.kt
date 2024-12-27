package scripts.wrMotherlode.behaviours.motherlode

import org.tribot.api2007.Camera
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.util.TribotRandom
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.calculators.ResourceCounter
import scripts.utils.failsafes.LastActionTracker
import scripts.utils.progress.webhook.DiscordNotifier
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
                    Login.login()
                }
            }

            selector {
                perform {
                    DiscordNotifier.notify(message = "${MyPlayer.getUsername()} is going strong at the Motherlode")
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
                    condition {
                        managers.stateManager.moveToNextState()
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("COLLECTING") }
                sequence {
                    condition {
                        // Check if we should be looting.
                        if (managers.sackManager.canBeFilled()) {
                            logger.debug("Sack isn't full yet, skipping collection..")
                            managers.stateManager.moveToNextState()
                            return@condition true
                        }

                        // BEGIN: walkToSackNode
                        val oreSackTile = WorldTile(3749, 5659, 0)

                        if (!oreSackTile.isOnMinimap || !oreSackTile.isVisible) {
                            Waiting.waitUntil {
                                logger.debug("Walking to oresack")
                                LocalWalking.walkTo(oreSackTile)

                                Waiting.wait(FatigueResolver.getMilliseconds())

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

                            Waiting.wait(FatigueResolver.getMilliseconds())
                            Inventory.getFilledSlots() > 2 // any better solution?
                        }

                        // We have collected, thus let's register the loot!
                        managers.progressionManager.registerLoot()

                        //END

                        // BEGIN: bankLootNode
                        val bankTask = BankTask.builder()
//                            .addInvItem(1275, Amount.of(1)) // Rune Pickaxe
                            .addInvItem(11920, Amount.of(1)) // Dragon Pickaxe
                            .addInvItem(2347, Amount.of(1)) // Hammer
                            .build()

                        Waiting.waitUntil {
                            if (!bankTask.isSatisfied() || Inventory.getFilledSlots() > 2) {
                                bankTask.execute()
                            }

                            Waiting.wait(FatigueResolver.getMilliseconds())

                            bankTask.isSatisfied()
                        }
                        //END

                        if (managers.sackManager.isEmpty()) {
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
                condition { managers.sackManager.canBeFilled() }
                sequence {
                    condition {
                        managers.stateManager.resetCycle("COLLECTING")
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("FILLING") }
                sequence {
                    fillHopperNode(logger, managers)
                    selector {
                        condition { !managers.sackManager.canBeFilled() }
                        condition {
                            Waiting.waitUntil {
                                Query.inventory()
                                    .nameEquals("Pay-dirt")
                                    .forEach {
                                        it.click("Drop")
                                    }

                                Waiting.wait(FatigueResolver.getMilliseconds())

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
                    perform {
                        Camera.setCameraAngle(TribotRandom.normal(95, 5))
                        Camera.setCameraRotation(TribotRandom.normal(340, 10))
                    }
                    ensureMineReadyInventory(logger, managers)
                    walkToVeinsNode(logger)
                    mineVeinsNode(logger, managers)
                    condition {
                        ResourceCounter.increment("Pay-dirt", Inventory.getCount("Pay-dirt"))
                        managers.stateManager.moveToNextState()
                    }
                }
            }

        }
    }
}