package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
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
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
        }
    }

    selector {
        condition { tripStateManager.isCurrentState("COLLECT_BARS") == true }
        sequence {
            // this isn't optimal, cuz when the ores are being smelted, the dispenser doesn't hold any bars yet..
//            selector {
//                condition { !barManager.dispenserHoldsBars() }
//                perform {
//                    tripStateManager.resetCycle("COLLECT_ORES")
//                }
//            }
            collectBarsNode(logger, barManager, tripStateManager)
        }
    }

    selector {
        condition {
            tripStateManager.isCurrentState("FILL_CONVEYOR") == true
        }

        loadOresNode(logger, barManager, tripStateManager)
    }

    selector {
        condition {
            tripStateManager.isCurrentState("COLLECT_ORES") == true
        }
        sequence {
            bankNode(logger, true)
            withdrawItemNode(logger, "Copper ore", 14, false)
            withdrawItemNode(logger, "Tin ore", 14, true)
            condition {
                tripStateManager.cycleStateFrom(
                    tripStateManager.getCurrentKey()
                )
            }
        }
    }
}