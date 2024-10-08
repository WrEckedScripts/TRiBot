package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIsOpenNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.behaviours.stamina.actions.sipStaminaPotion
import scripts.wrBlastFurnace.gui.Settings
import scripts.wrBlastFurnace.managers.*

fun IParentNode.smeltBarsNode(
    logger: Logger,
    dispenserManager: DispenserManager,
    meltingPotManager: MeltingPotManager,
    tripStateManager: TripStateManager,
    cameraManager: CameraManager,
    staminaManager: StaminaManager,
    playerRunManager: PlayerRunManager
) = sequence {

    /**
     * Ensures that we only allow full inventories, when it's either:
     * - full of COAL
     * - full of BASE ore (ex: Iron ore)
     * - And we're not expected to bank
     */
    selector {
        condition { Inventory.isEmpty() }
        selector {
            condition {
                Query.inventory().nameEquals(tripStateManager.coalOre.name).count() == tripStateManager.coalOre.quantity
            }
            condition {
                Query.inventory().nameEquals(tripStateManager.baseOre.name).count() == tripStateManager.baseOre.quantity
            }
        }
        condition { tripStateManager.isCurrentState("BANK_BARS") == false }
        sequence {
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
        }
    }

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
            collectBarsNode(logger, dispenserManager, tripStateManager)
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
        condition { dispenserManager.holdsBars() }
        condition { Inventory.isFull() }
        sequence {
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
            sipStaminaPotion(logger, staminaManager, playerRunManager)
            withdrawItemNode(
                logger,
                tripStateManager.baseOre.name(),
                tripStateManager.baseOre.quantity(),
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
        condition { dispenserManager.holdsBars() }
        condition { Inventory.isEmpty() }
        sequence {
            loadOresNode(logger)
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
            perform {
                if (Settings.preWalkChecked) {
                    val preWalkTile = WorldTile(1939, 4963, 0)
                    LocalWalking.walkTo(preWalkTile)
                }

                Lottery.execute(0.78) {
                    cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    /**
     *  Given we are tasked to process coal
     *  And the melting pot contains more than our threshold in terms of coal stock
     *  We simply skip the coal task, to prevent any overfilling
     */
    selector {
        condition { tripStateManager.isCurrentState("PROCESS_COAL") == true }
        condition { !meltingPotManager.containsCoalMoreThan(112) }
        condition {
            tripStateManager.cycleStateFrom(
                tripStateManager.getCurrentKey()
            )
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
        condition { dispenserManager.holdsBars() }
        condition { Inventory.isFull() }
        sequence {
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
            sipStaminaPotion(logger, staminaManager, playerRunManager)

            withdrawItemNode(
                logger,
                tripStateManager.coalOre.name(),
                tripStateManager.coalOre.quantity(),
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
        condition { dispenserManager.holdsBars() }
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