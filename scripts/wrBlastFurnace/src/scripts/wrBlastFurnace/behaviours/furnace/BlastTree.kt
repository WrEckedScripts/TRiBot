package scripts.wrBlastFurnace.behaviours.furnace

import org.tribot.script.sdk.*
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.utils.behaviours.banking.actions.bankNode
import scripts.utils.behaviours.banking.actions.withdrawItemNode
import scripts.utils.progress.webhook.DiscordNotifier
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIceGlovesAreWorn
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIsOpenNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.payForemanNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.smeltBarsNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.topupCofferNode
import scripts.wrBlastFurnace.behaviours.setup.actions.moveToFurnaceNode
import scripts.wrBlastFurnace.behaviours.setup.validation.MoveToFurnaceValidation
import scripts.wrBlastFurnace.gui.Settings
import scripts.wrBlastFurnace.managers.Container

fun getBlastTree(
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

            //World hopping
            selector {
                condition { Settings.getWorld() == WorldHopper.getCurrentWorld() }
                condition {
                    logger.debug("WorldHopping - Let's hop from ${WorldHopper.getCurrentWorld()} to ${Settings.getWorld()}")
                    Waiting.waitUntil(5000) {
                        WorldHopper.hop(Settings.getWorld())
                    }
                }
            }

            /**
             * Ensure that we're working in resizableMode
             */
            selector {
                condition { !Settings.getHideChatbox() }
                condition { Options.isResizableModeEnabled() }
                condition {
                    Options.setResizableModeType(Options.ResizableType.RESIZABLE_CLASSIC)
                }
            }

            /**
             * Ensures the chatbox is closed (when in resizableMode)
             */
            selector {
                condition { !Settings.getHideChatbox() }
                condition { !Chatbox.isOpen() && Options.isResizableModeEnabled() }
                condition {
                    Chatbox.hide()
                }
            }

            // Will send a screenshot every x minutes
            selector {
                condition { !Settings.usesDiscord() }
                //todo we need to pass through the interval somehow
                // and get rid of the "perform" as this is always executed iirc.
                perform {
                    DiscordNotifier.notify()
                }
            }

            /**
             * Make sure we're at the Blast Furnace Area
             */
            selector {
                condition { MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                moveToFurnaceNode(logger)
            }

            selector {
                condition { managers.playerRunManager.satisfiesRunExpectation() }
                perform {
                    managers.playerRunManager.enableRun()
                }
            }

            /**
             * Serves as a failsafe, as long as the dispenser holds any kind of bar, and we're not in the collecting stage
             * Ensure that we switch to that state and first withdraw any bars from the dispenser.
             * This prevents blocking the conveyor belt
             * - Could happen due to re-starting the script
             * - Could happen when a script is stopped/ended and is started again
             * Or any other partial cycle flow occurrence
             */
            selector {
                condition { managers.tripStateManager.isCurrentState("COLLECT_BARS") == false }
                condition { !managers.dispenserManager.holdsBars() }
                sequence {
                    selector {
                        condition { Inventory.isEmpty() }
                        sequence {
                            ensureIsOpenNode(logger)
                            bankNode(logger, true, true)
                        }
                    }
                    condition {
                        managers.tripStateManager.resetCycle("COLLECT_BARS")
                        managers.tripStateManager.isCurrentState("COLLECT_BARS") == false
                    }
                }
            }

            /**
             * So, now we're inside the BF area
             * Let's make sure that we wear our ice gloves
             * - If not, we should bank, withdraw and wear them
             * - If this fails, then we can't run the BF script.
             */
            selector {
                condition {
                    Equipment.contains("Ice gloves")
                }
                ensureIceGlovesAreWorn(logger)
            }

            selector {
                condition { managers.upkeepManager.havePaidForeman() }
                condition { managers.upkeepManager.playerHoldsEnoughCoins() }
                sequence {
                    ensureIsOpenNode(logger)
                    bankNode(logger, true, false)
                    withdrawItemNode(logger, "Coins", 2500, true)
                    payForemanNode(logger, managers.upkeepManager, managers.tripStateManager)
                }
            }

            selector {
                condition { managers.upkeepManager.haveFilledCoffer() }
                condition { managers.upkeepManager.playerHoldsEnoughCoins(managers.upkeepManager.getCofferTopupAmount()) }
                sequence {
                    ensureIsOpenNode(logger)
                    bankNode(logger, true, false)

                    withdrawItemNode(logger, "Coins", managers.upkeepManager.getCofferTopupAmount(), true)
                    topupCofferNode(
                        logger,
                        managers.upkeepManager,
                        managers.tripStateManager,
                        managers.dispenserManager
                    )

                    ensureIsOpenNode(logger)
                    bankNode(logger, true, false)
                }
            }

            selector {
                condition { !MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                condition { !managers.upkeepManager.haveFilledCoffer() }
                condition { !managers.upkeepManager.havePaidForeman() }
                sequence {
                    smeltBarsNode(logger, managers)
                }
            }
        }
    }
}