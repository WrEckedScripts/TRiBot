package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIsOpenNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.behaviours.stamina.actions.sipStaminaPotion
import scripts.wrBlastFurnace.managers.*

fun IParentNode.smeltBarsNode(
    logger: Logger,
    barManager: BarManager,
    tripStateManager: TripStateManager,
    cameraManager: CameraManager,
    staminaManager: StaminaManager,
    playerRunManager: PlayerRunManager
) = sequence {
    selector {
        condition { tripStateManager.isCurrentState("BANK_BARS") == true }
        sequence {
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
            sipStaminaPotion(logger, staminaManager, playerRunManager)
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
            perform {
                Lottery.execute(1.0) {
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
                Lottery.execute(0.6) {
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
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
            sipStaminaPotion(logger, staminaManager, playerRunManager)
            withdrawItemNode(
                logger,
                tripStateManager.baseOre.name,
                tripStateManager.baseOre.quantity,
                true
            )
            perform {
                Lottery.execute(0.6) {
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
                //TODO Randomly pick one of the touching tiles?
                // - Could be GUI option to pre-walk
                val preWalkTile = WorldTile(1939, 4963, 0)
                LocalWalking.walkTo(preWalkTile)

                Lottery.execute(0.6) {
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
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
            sipStaminaPotion(logger, staminaManager, playerRunManager)
            withdrawItemNode(
                logger,
                tripStateManager.coalOre.name,
                tripStateManager.coalOre.quantity,
                true
            )
            perform {
                Lottery.execute(0.6) {
                    cameraManager.randomize(zoom = false)
                }
            }
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
        condition { tripStateManager.isCurrentState("PROCESS_COAL") == true }
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
                Lottery.execute(0.6) {
                    cameraManager.randomize(zoom = false)
                }
            }
        }
    }
}