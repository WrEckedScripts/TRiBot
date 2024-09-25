package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.managers.BarManager
import scripts.wrBlastFurnace.managers.TripStateManager

fun IParentNode.smeltBarsNode(
    logger: Logger,
    barManager: BarManager,
    tripStateManager: TripStateManager
) = sequence {

    selector {
        condition { tripStateManager.isCurrentState("BANK_BARS") == true }
        sequence {
            bankNode(logger, true, false)
            condition {
                !Inventory.contains(tripStateManager.bar)
            }
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
        }
    }

    selector {
        condition { tripStateManager.isCurrentState("COLLECT_BARS") == true }
        sequence {
            collectBarsNode(logger, barManager, tripStateManager)
        }
    }

    selector {
        condition { tripStateManager.isCurrentState("PROCESS_BASE") == true }
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

    selector {
        condition { tripStateManager.isCurrentState("PROCESS_BASE") == true }
        condition { Inventory.isEmpty() }
        sequence {
            loadOresNode(logger)
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
        }
    }

    selector {
        condition { tripStateManager.isCurrentState("PROCESS_COAL") == true }
        condition { Inventory.isFull() } //can sometimes double bank, slight wait perhaps?
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

    selector {
        condition { tripStateManager.isCurrentState("PROCESS_COAL") == true }
        condition { Inventory.isEmpty() } //can sometimes double bank, slight wait perhaps?
        sequence {
            loadOresNode(logger)
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
        }
    }
}