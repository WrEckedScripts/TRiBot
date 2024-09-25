package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.managers.BarManager
import scripts.wrBlastFurnace.managers.CameraManager
import scripts.wrBlastFurnace.managers.TripStateManager

fun IParentNode.smeltBarsNode(
    logger: Logger,
    barManager: BarManager,
    tripStateManager: TripStateManager,
    cameraManager: CameraManager
) = sequence {

    selector {
        condition { tripStateManager.isCurrentState("BANK_BARS") == true }
        sequence {
            bankNode(logger, depositInventory = true, close = false)
            condition {
                !Inventory.contains(tripStateManager.bar)
            }
            condition {
                logger.debug("Cycling state:")
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
            perform {
                Lottery(logger).execute(0.3) {
                    cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    selector {
        condition { tripStateManager.isCurrentState("COLLECT_BARS") == true }
        sequence {
            collectBarsNode(logger, barManager, tripStateManager)
            perform {
                Lottery(logger).execute(0.3) {
                    cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    /**
     * Given we are processing base
     * And the dispenser is not holding bars
     * And our inventory is full
     *
     * We will withdraw a coal
     */
    selector {
        condition { tripStateManager.isCurrentState("PROCESS_BASE") == true }
        condition { barManager.dispenserHoldsBars() }
        condition { Inventory.isFull() }
        sequence {
            bankNode(logger, true)
            withdrawItemNode(
                logger,
                tripStateManager.baseOre.name,
                tripStateManager.baseOre.quantity,
                true
            )
        }
    }

    /**
     * Given we are processing base
     * And the dispenser is not holding bars
     * And our inventory is full
     *
     * We will load our ores to the conveyor
     */
    selector {
        condition { tripStateManager.isCurrentState("PROCESS_BASE") == true }
        condition { barManager.dispenserHoldsBars() }
        condition { Inventory.isEmpty() }
        sequence {
            loadOresNode(logger)
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
            perform {
                Lottery(logger).execute(0.3) {
                    cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    /**
     * Given we are processing coal
     * And the dispenser is not holding bars
     * And our inventory is empty
     *
     * We will withdraw a coal
     */
    selector {
        condition { tripStateManager.isCurrentState("PROCESS_COAL") == true }
        condition { barManager.dispenserHoldsBars() }
        condition { Inventory.isFull() }
        sequence {
            bankNode(logger, true)
            withdrawItemNode(
                logger,
                tripStateManager.coalOre.name,
                tripStateManager.coalOre.quantity,
                true
            )
        }
    }

    /**
     * Given we are processing coal
     * And the dispenser is not holding bars
     * And our inventory is not empty
     *
     * We will load our ores to the conveyor
     */
    selector {
        condition { tripStateManager.isCurrentState("PROCESS_COAL") == true}
        condition { barManager.dispenserHoldsBars() }
        condition { Inventory.isEmpty() }
        sequence {
            loadOresNode(logger)
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
            perform {
                Lottery(logger).execute(0.3) {
                    cameraManager.randomize(zoom = false)
                }
            }
        }
    }
}