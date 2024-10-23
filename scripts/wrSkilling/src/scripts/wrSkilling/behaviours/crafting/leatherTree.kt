package scripts.wrSkilling.behaviours.crafting

import org.tribot.script.sdk.*
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.antiban.MiniBreak
import scripts.wrSkilling.managers.Container
import scripts.wrSkilling.materials.CraftableResolver

fun LeatherTree(
    logger: Logger,
    managers: Container
) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        sequence {
            selector {
                condition { Login.isLoggedIn() }
                condition { Login.login() }
            }

//        selector {
//            //TODO G.E. location
//        }

            // Withdrawing supplies
            selector {
                condition { managers.stateManager.isCurrentState("WITHDRAW_SUPPLIES") }
                sequence {
                    condition { Bank.ensureOpen() }

                    //TODO, keep needles and threads.
                    // - Some sort of ignore list perhaps, that if the inventory contains items
                    // - from this ignore list, we prevent a full deposit?
                    // - Might be a BankTask thingy?

                    condition {
                        val task = BankTask.builder()
                            // Needle
                            .addInvItem(1733, Amount.range(5, 20_000))
                            // Threads
                            .addInvItem(1734, Amount.range(10, 20_000))
                            // Leather
                            .addInvItem(1741, Amount.range(1, 26))
                            .build()

                        task.execute()

                        //TODO, can be used to prevent bank open/close
                        logger.debug(task.isSatisfied())
                        logger.debug(task.hasRequiredItems())

                        Waiting.waitUntil(20_000) {
                            Waiting.waitNormal(1_000, 100)
                            logger.debug("Waiting until bankTask closes the bank.")
                            !Bank.isOpen()
                        }
                    }

                    condition {
                        managers.stateManager.cycleStateFrom(
                            managers.stateManager.getCurrentKey()
                        )
                    }
                }
            }

            // Crafting correct leather item, level based
            selector {
                condition { managers.stateManager.isCurrentState("CRAFT_LEATHER") }
                sequence {
                    condition {
                        Query.inventory().nameEquals("Leather").count() > 1
                    }

                    condition {
                        // Find needle and use it on a random Leather
                        Waiting.waitUntil(5000) {
                            Query.inventory()
                                .nameEquals("Needle")
                                .findFirst()
                                .get()
                                .useOn(
                                    Query.inventory()
                                        .nameEquals("Leather")
                                        .findRandom()
                                        .get()
                                )
                        }

                        Waiting.waitUntil(2_000) {
                            MakeScreen.isOpen()
                            MakeScreen.makeAll(CraftableResolver.getItem())
                        }

                        Waiting.waitNormal(750, 43)
                        Lottery.execute(1.0) {
                            MiniBreak.leave()
                        }

                        Waiting.waitUntil(2_000) {
                            MyPlayer.isAnimating()
                        }
                    }

                    condition {
                        managers.stateManager.cycleStateFrom(
                            managers.stateManager.getCurrentKey()
                        )
                    }
                }
            }

            // Waiting selector, before we re-bank or re-init crafting
            // for example, we need to re-start crafting, once a level-up occured
            selector {
                condition { managers.stateManager.isCurrentState("WAITING") }
                sequence {
                    selector {
                        condition { !ChatScreen.isClickContinueOpen() }
                        sequence {
                            condition {
                                managers.stateManager.resetCycle("CRAFT_LEATHER")
                                Waiting.waitNormal(400, 35)
                                managers.stateManager.getCurrentKey() == "CRAFT_LEATHER"
                            }
                        }
                    }

                    selector {
                        condition {
                            Query.inventory()
                                .nameEquals("Leather")
                                .count() > 0
                        }
                        sequence {
                            condition {
                                managers.stateManager.cycleStateFrom(
                                    managers.stateManager.getCurrentKey()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}