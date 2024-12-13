package scripts.wrFoundry.behaviours

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrFoundry.enums.Stage
import scripts.wrFoundry.managers.Container
import scripts.wrFoundry.states.ActiveState
import scripts.wrFoundry.tasks.processing.ProcessingTask
import scripts.wrFoundry.tasks.temperature.CoolDown
import scripts.wrFoundry.tasks.temperature.HeatUp

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
//                    ActiveState.get()?.displayName.orEmpty()
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
                condition { !(ActiveState.get() == Stage.TRIP_HAMMER) }
                sequence {
//                    condition {
//                        logger.debug("Checking currents ${ActiveState.get()?.heat?.displayName == HeatVarbit.get()?.displayName}")
//                        // Check if the current heat state we need is the active heat level from the varbit
//                        ActiveState.get()?.heat?.displayName != HeatVarbit.get()?.displayName
//                    }
                    // Check if we should heat or cool
                    selector {
                        condition {
                            logger.debug("Should exec heatup")
                            !HeatUp(ActiveState.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing heatup")
                                HeatUp(ActiveState.get()!!.heat).execute()
                            }
                        }
                    }
                    selector {
                        condition {
                            logger.debug("Should exec coolDown")
                            !CoolDown(ActiveState.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing coolDown")
                                CoolDown(ActiveState.get()!!.heat).execute()
                            }
                        }
                    }
                    // Move to trip hammering here of after this sequence?
                    condition {
                        ProcessingTask("Trip hammer", Stage.TRIP_HAMMER).execute()
                    }
                }
            }

            selector {
                condition { !(ActiveState.get() == Stage.GRINDSTONE) }
                sequence {
                    // Check if we should heat or cool
                    selector {
                        condition {
                            logger.debug("Should exec heatup")
                            !HeatUp(ActiveState.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing heatup")
                                HeatUp(ActiveState.get()!!.heat).execute()
                            }
                        }
                    }
                    selector {
                        condition {
                            logger.debug("Should exec coolDown")
                            !CoolDown(ActiveState.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing coolDown")
                                CoolDown(ActiveState.get()!!.heat).execute()
                            }
                        }
                    }
                    condition {
                        ProcessingTask("Grindstone", Stage.GRINDSTONE).execute()
                    }
                }
            }

            selector {
                condition { !(ActiveState.get() == Stage.POLISHING_WHEEL) }
                sequence {
                    // Check if we should heat or cool
                    selector {
                        condition {
                            logger.debug("Should exec heatup")
                            logger.error("RESULT: ${!HeatUp(ActiveState.get()!!.heat).shouldExecute()}")
                            !HeatUp(ActiveState.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing heatup")
                                logger.error("SUBRESULT: ")
                                HeatUp(ActiveState.get()!!.heat).execute()
                            }
                        }
                    }
                    selector {
                        condition {
                            logger.debug("Should exec coolDown")
                            !CoolDown(ActiveState.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing coolDown")
                                CoolDown(ActiveState.get()!!.heat).execute()
                            }
                        }
                    }
                    condition {
                        ProcessingTask("Polishing wheel", Stage.POLISHING_WHEEL).execute()
                    }
                }
            }

            //TODO selector for fetching a task

            //TODO selector for handing-in a task

            //TODO selector for getting a new task

            //TODO selector for filling the crucible, pouring into the mould and selecting the combination
        }
    }
}