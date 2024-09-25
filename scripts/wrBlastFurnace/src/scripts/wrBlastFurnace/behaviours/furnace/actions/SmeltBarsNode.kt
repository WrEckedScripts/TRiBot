package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.managers.BarManager

fun IParentNode.smeltBarsNode(
    logger: Logger,
    barManager: BarManager
) = sequence {

    /**
     * For bronze:
     * do one trip of 14 copper and 14 tin
     * - wait a bit, and collect, then bank once inventory contains bars.
     */
    //"Put-ore-on"
    // Conveyor belt

    selector {
        condition { barManager.hasCollected() }
        sequence {
            selector {
                condition {
                    logger.info("selector:SmeltBarsNode.barManager.hasCollected() | inv: ${Inventory.isEmpty()}")
                    Inventory.isEmpty()
                }
                // TODO, needs to ensure the inventory is deposited + closed
                // - added todo to change perform > condition
                bankNode(logger, true, true)
            }
            collectBarsNode(logger, barManager)
        }
    }

    selector {
        condition {
            logger.info("selector:SmeltBarsNode.condition.!hasCollected")
            !barManager.hasCollected()
        }
        condition {
            logger.info("- selector:SmeltBarsNode.condition.!invHoldsOres")
            !barManager.inventoryHoldsOres()
        }
        loadOresNode(logger, barManager)
    }

    selector {
        condition {
            logger.info("selector:SmeltBarsNode.condition.invHolds")
            barManager.inventoryHoldsOres()
        }
        sequence {
            bankNode(logger, true)
            withdrawItemNode(logger, "Copper ore", 14, false)
            withdrawItemNode(logger, "Tin ore", 14, true)
        }
    }
}