package scripts.wrBlastFurnace.behaviours.furnace

import org.tribot.script.sdk.Chatbox
import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Options
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.utils.progress.webhook.DiscordNotifier
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIsOpenNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.payForemanNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.smeltBarsNode
import scripts.wrBlastFurnace.behaviours.furnace.actions.topupCofferNode
import scripts.wrBlastFurnace.behaviours.setup.actions.moveToFurnaceNode
import scripts.wrBlastFurnace.behaviours.setup.validation.MoveToFurnaceValidation
import scripts.wrBlastFurnace.managers.*

fun getBlastTree(
    logger: Logger,
    upkeepManager: UpkeepManager,
    dispenserManager: DispenserManager,
    meltingPotManager: MeltingPotManager,
    tripStateManager: TripStateManager,
    playerRunManager: PlayerRunManager,
    staminaManager: StaminaManager,
    cameraManager: CameraManager
) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        sequence {
            /**
             * Ensures that we're logged in, after we get disconnected for example
             * - Main login action, is handled within the startupTree
             */
//            loginNode(logger)

            /**
             * TODO, below untested parts, should be within a dedicated node/setup tree
             *  - together with login logic.
             */

            //TODO untested
            selector {
                condition { Options.isResizableModeEnabled() }
                perform {
                    Options.setResizableModeType(Options.ResizableType.RESIZABLE_CLASSIC)
                }
            }

            //TODO untested
            selector {
                condition { !Chatbox.isOpen() && Options.isResizableModeEnabled() }
                perform {
                    Chatbox.hide()
                }
            }

            // Will send a screenshot every x minutes
            selector {
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
                condition { playerRunManager.satisfiesRunExpectation() }
                perform {
                    playerRunManager.enableRun()
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
                condition { tripStateManager.isCurrentState("COLLECT_BARS") == false }
                condition { !dispenserManager.holdsBars() }
                sequence {
                    selector {
                        condition { Inventory.isEmpty() }
                        sequence {
                            ensureIsOpenNode(logger)
                            bankNode(logger, true, true)
                        }
                    }
                    condition {
                        tripStateManager.resetCycle("COLLECT_BARS")
                        tripStateManager.isCurrentState("COLLECT_BARS") == false
                    }
                }
            }

            selector {
                condition { upkeepManager.havePaidForeman() }
                condition { upkeepManager.playerHoldsEnoughCoins() }
                sequence {
                    ensureIsOpenNode(logger)
                    bankNode(logger, true, false)
                    withdrawItemNode(logger, "Coins", 2500, true)
                    payForemanNode(logger, upkeepManager, tripStateManager)
                }
            }

            selector {
                condition { upkeepManager.haveFilledCoffer() }
                condition { upkeepManager.playerHoldsEnoughCoins(upkeepManager.getCofferTopupAmount()) }
                sequence {
                    ensureIsOpenNode(logger)
                    bankNode(logger, true, false)

                    withdrawItemNode(logger, "Coins", upkeepManager.getCofferTopupAmount(), true)
                    topupCofferNode(logger, upkeepManager, tripStateManager, dispenserManager)

                    ensureIsOpenNode(logger)
                    bankNode(logger, true, false)
                }
            }

            selector {
                condition { !MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
                condition { !upkeepManager.haveFilledCoffer() }
                condition { !upkeepManager.havePaidForeman() }
                sequence {
                    smeltBarsNode(
                        logger,
                        dispenserManager,
                        meltingPotManager,
                        tripStateManager,
                        cameraManager,
                        staminaManager,
                        playerRunManager
                    )
                }
            }
        }
    }
}