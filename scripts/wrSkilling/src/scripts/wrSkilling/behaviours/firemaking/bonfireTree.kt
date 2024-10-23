package scripts.wrSkilling.behaviours.firemaking

import org.tribot.script.sdk.*
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.wrSkilling.managers.Container
import kotlin.jvm.optionals.getOrNull

fun BonfireTree(
    logger: Logger,
    managers: Container
) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        sequence {
            selector {
                condition { Login.isLoggedIn() }
                condition { Login.login() }
            }

            //TODO move to G.E. / skilling location

            selector {
                condition { managers.stateManager.isCurrentState("WITHDRAW_SUPPLIES") }
                sequence {
                    condition { Bank.ensureOpen() }
                    condition {
                        val task = BankTask.builder()
                            // Tinderbox
                            .addInvItem(590, Amount.of(1))
                            // Logs
                            .addInvItem(1511, Amount.range(1, 27))
                            .build()

                        task.execute()

                        Waiting.waitUntil(20_000) {
                            Waiting.waitNormal(1_000, 342)
                            !Bank.isOpen()
                        }
                    }
                    condition {
                        val bonfireNearby = Query.gameObjects()
                            .idEquals(49927)
                            .findClosest()

                        if (bonfireNearby.isEmpty) {
                            managers.stateManager.cycleStateFrom(
                                managers.stateManager.getCurrentKey()
                            )
                        } else {
                            managers.stateManager.resetCycle("FEED_BONFIRE")
                        }
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("LIGHTING_BONFIRE") }
                sequence {
                    condition {
                        // this could mean, that we move quite away from our lighting spot
                        // will fix this eventually, but for now, this is enough for personal usage.
                        var regularFire = Query.gameObjects()
                            .idEquals(26185)
                            .findFirst()

                        // there's no regular fire, make one
                        if (regularFire.isEmpty) {
                            // walk to tile
                            Waiting.waitUntil {
                                val tile = WorldTile(3184, 3431, 0)
                                LocalWalking.walkTo(tile)
                                Waiting.waitNormal(800, 90)
                                MyPlayer.getTile() == tile
                            }

                            // use tinderbox on a log or vice versa
                            Query.inventory()
                                .nameEquals("Logs")
                                .findRandom()
                                .getOrNull()
                                ?.useOn(
                                    Query.inventory()
                                        .nameEquals("Tinderbox")
                                        .findFirst()
                                        .get()
                                )

                            Waiting.waitUntil(120_000) {
                                regularFire = Query.gameObjects()
                                    .idEquals(26185)
                                    .findFirst()

                                regularFire.isPresent
                            }
                        }

                        // add log to the fire
                        Waiting.waitUntil(5_000) {
                            Query.inventory()
                                .nameEquals("Logs")
                                .findRandom()
                                .get()
                                .useOn(
                                    regularFire.get()
                                )

                            Waiting.waitNormal(700, 55)

                            MakeScreen.isOpen()
                        }

                        // makeAll
                        Waiting.waitUntil(5_000) {
                            logger.debug("Burning logs")

                            Waiting.waitNormal(700, 55)

                            MakeScreen.makeAll("Logs")
                        }

                        logger.debug("LIGHTING_BONFIRE > Next up state")
                        managers.stateManager.resetCycle("WAITING")
                    }
                }
//                        selector {
//                            condition {
//                                Query.inventory()
//                                    .nameEquals("Logs")
//                                    .count() > 0
//                            }
//                            condition {
//                                managers.stateManager.cycleStateFrom(
//                                    managers.stateManager.getCurrentKey()
//                                )
//                            }
//                        }
            }

            selector {
                condition { managers.stateManager.isCurrentState("FEED_BONFIRE") }
                sequence {
                    condition {
                        val bonFire = Query.gameObjects()
                            .idEquals(49927)
                            .findClosest()

                        if (bonFire.isPresent) {
                            val bonFireObject = bonFire.get()

                            Waiting.waitUntil(15_000) {
                                bonFireObject.interact("Tend-to")
                                Waiting.waitNormal(600, 32)
                                MakeScreen.isOpen()

                                Waiting.waitNormal(700, 55)

                                MakeScreen.makeAll("Logs")
                            }

                            managers.stateManager.resetCycle("WAITING")
                        } else {
                            managers.stateManager.resetCycle("LIGHTING_BONFIRE")
                        }
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("WAITING") }
                sequence {
                    selector {
                        condition { !ChatScreen.isClickContinueOpen() }
                        condition {
                            managers.stateManager.resetCycle("FEED_BONFIRE")
                            Waiting.waitNormal(400, 35)
                            managers.stateManager.getCurrentKey() == "FEED_BONFIRE"
                        }
                    }
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState("WAITING") }
                sequence {
                    selector {
                        condition {
                            Query.inventory()
                                .nameEquals("Logs")
                                .count() > 0
                        }
                        condition {
                            managers.stateManager.resetCycle("WITHDRAW_SUPPLIES")
                        }
                    }
                }
            }
        }
    }
}