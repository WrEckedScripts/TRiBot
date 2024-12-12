package scripts.wrFoundry.behaviours

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrFoundry.enums.InteractableMachine
import scripts.wrFoundry.enums.Stage
import scripts.wrFoundry.managers.Container
import scripts.wrFoundry.states.ActiveState
import scripts.wrFoundry.states.HeatVarbit

fun getFoundryTree(
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

            //TODO simply for debugging looping
//            selector {
//                condition {
//                    ActiveState.get(logger)?.displayName.orEmpty()
//                    logger.info("Waiting for a sec")
//                    Waiting.wait(3_000)
//
//                    false // Always trigger
//                }
//            }

            /**
             * The actual mechanics
             */
            selector {
                condition { !(ActiveState.get(logger) == Stage.TRIP_HAMMER) }
                sequence {
                    selector {
                        condition {
                            // Check if the current heat state we need is the active heat level from the varbit
                            ActiveState.get(logger)?.heat?.displayName == HeatVarbit.get()?.displayName
                        }
                        condition {
                            logger.error("We need to either cool down or heat up")
                            //heat up or cool down calculation + execution
                            true
                        }
                    }
                    condition {
                        logger.debug("inside trip hammer, we're heated up enough?")
                        // determine heat value at and to which we aim
                        // calculate the difference and determine for fast / steady heating option.
                        Waiting.wait(2_000)
                        logger.info("[Mimic] - 'Use ${InteractableMachine.HAMMER.objectName}'")
                        false
                    }
                }
                perform { logger.warn("[TRIP_HAMMER_NODE]") }
            }

            selector {
                condition { !(ActiveState.get(logger) == Stage.GRINDSTONE) }
                sequence {
                    selector {
                        condition {
                            // Check if the current heat state we need is the active heat level from the varbit
                            ActiveState.get(logger)?.heat?.displayName == HeatVarbit.get()?.displayName
                        }
                        condition {
                            logger.error("We need to either cool down or heat up")
                            //heat up or cool down calculation + execution
                            true
                        }
                    }
                    condition {
                        logger.debug("inside trip hammer, we're heated up enough?")
                        // determine heat value at and to which we aim
                        // calculate the difference and determine for fast / steady heating option.
                        Waiting.wait(2_000)
                        logger.info("[Mimic] - 'Use ${InteractableMachine.GRINDSTONE.objectName}'")
                        false
                    }
                }
                perform { logger.warn("[GRINDSTONE_NODE]") }
            }

            selector {
                condition { !(ActiveState.get(logger) == Stage.POLISHING_WHEEL) }
                sequence {
                    selector {
                        condition {
                            // Check if the current heat state we need is the active heat level from the varbit
                            ActiveState.get(logger)?.heat?.displayName == HeatVarbit.get()?.displayName
                        }
                        condition {
                            logger.error("We need to either cool down or heat up")
                            //heat up or cool down calculation + execution
                            true
                        }
                    }
                    condition {
                        logger.debug("inside trip hammer, we're heated up enough?")
                        // determine heat value at and to which we aim
                        // calculate the difference and determine for fast / steady heating option.
                        Waiting.wait(2_000)
                        logger.info("[Mimic] - 'Use ${InteractableMachine.POLISHING_WHEEL.objectName}'")
                        false
                    }
                }
                perform { logger.warn("[POLISHING_WHEEL]") }
            }

            //TODO selector for fetching a task

            //TODO selector for handing-in a task

            //TODO selector for getting a new task

            //TODO selector for filling the crucible, pouring into the mould and selecting the combination
        }
    }
}