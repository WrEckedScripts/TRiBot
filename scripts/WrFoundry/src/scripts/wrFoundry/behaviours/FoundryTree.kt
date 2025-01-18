package scripts.wrFoundry.behaviours

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrFoundry.enums.InteractableMachine
import scripts.wrFoundry.enums.Stage
import scripts.wrFoundry.managers.Container
import scripts.wrFoundry.states.Commission
import scripts.wrFoundry.states.CurrentProcessingTask
import scripts.wrFoundry.tasks.prepare.ReceiveCommissionTask
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

            selector {
                condition { Login.isLoggedIn() && MyPlayer.isMember() }
                condition {
                    throw Exception("Ran out of membership..")
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
                condition { !Commission().needsTask() }
                sequence {
                    condition {
                        Waiting.wait(2_500)
                        logger.error("Waiting on new task")
                        ReceiveCommissionTask(managers).execute()
                        false
                    }
                }
            }

            selector {
                condition { !(CurrentProcessingTask.get() == Stage.TRIP_HAMMER) }
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
                            !HeatUp(CurrentProcessingTask.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing heatup")
                                HeatUp(CurrentProcessingTask.get()!!.heat).execute()
                            }
                        }
                    }
                    selector {
                        condition {
                            logger.debug("Should exec coolDown")
                            !CoolDown(CurrentProcessingTask.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing coolDown")
                                CoolDown(CurrentProcessingTask.get()!!.heat).execute()
                            }
                        }
                    }
                    // Move to trip hammering here of after this sequence?
                    condition {
                        ProcessingTask(InteractableMachine.HAMMER, Stage.TRIP_HAMMER).execute()
                    }
                }
            }

            selector {
                condition { !(CurrentProcessingTask.get() == Stage.GRINDSTONE) }
                sequence {
                    // Check if we should heat or cool
                    selector {
                        condition {
                            logger.debug("Should exec heatup")
                            !HeatUp(CurrentProcessingTask.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing heatup")
                                HeatUp(CurrentProcessingTask.get()!!.heat).execute()
                            }
                        }
                    }
                    selector {
                        condition {
                            logger.debug("Should exec coolDown")
                            !CoolDown(CurrentProcessingTask.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing coolDown")
                                CoolDown(CurrentProcessingTask.get()!!.heat).execute()
                            }
                        }
                    }
                    condition {
                        ProcessingTask(InteractableMachine.GRINDSTONE, Stage.GRINDSTONE).execute()
                    }
                }
            }

            selector {
                condition { !(CurrentProcessingTask.get() == Stage.POLISHING_WHEEL) }
                sequence {
                    // Check if we should heat or cool
                    selector {
                        condition {
                            logger.debug("Should exec heatup")
                            logger.error("RESULT: ${!HeatUp(CurrentProcessingTask.get()!!.heat).shouldExecute()}")
                            !HeatUp(CurrentProcessingTask.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing heatup")
                                logger.error("SUBRESULT: ")
                                HeatUp(CurrentProcessingTask.get()!!.heat).execute()
                            }
                        }
                    }
                    selector {
                        condition {
                            logger.debug("Should exec coolDown")
                            !CoolDown(CurrentProcessingTask.get()!!.heat).shouldExecute()
                        }
                        sequence {
                            condition {
                                logger.debug("Executing coolDown")
                                CoolDown(CurrentProcessingTask.get()!!.heat).execute()
                            }
                        }
                    }
                    condition {
                        ProcessingTask(InteractableMachine.POLISHING_WHEEL, Stage.POLISHING_WHEEL).execute()
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