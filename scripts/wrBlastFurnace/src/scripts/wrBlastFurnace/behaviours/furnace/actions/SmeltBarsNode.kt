package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.behaviours.banking.actions.bankNode
import scripts.utils.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIsOpenNode
import scripts.wrBlastFurnace.behaviours.furnace.failsafes.SmithingArea
import scripts.wrBlastFurnace.behaviours.stamina.actions.sipStaminaPotion
import scripts.wrBlastFurnace.gui.Settings
import scripts.wrBlastFurnace.managers.Container

fun IParentNode.smeltBarsNode(
    logger: Logger,
    managers: Container
) = sequence {

    /**
     * Since the BF area, contains a Smithing area, which is fenced off,
     * Given it could happen, that our Player somehow walks into this "trap"
     * We need to escape this unusable area.
     *
     * @experimental Untested this, we need to add the correct WorldTiles and see if our player escapes the smithing room
     */
    selector {
        condition { !SmithingArea.isInsideArea() }
        condition {
            SmithingArea.handleEscape()
        }
    }

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
                Query.inventory().nameEquals(managers.tripStateManager.secondaryOre?.name)
                    .count() == managers.tripStateManager.secondaryOre?.quantity
            }
            condition {
                Query.inventory().nameEquals(managers.tripStateManager.baseOre.name)
                    .count() == managers.tripStateManager.baseOre.quantity
            }
        }
        condition { managers.tripStateManager.isCurrentState("BANK_BARS") == false }
        sequence {
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
        }
    }

    selector {
        condition { managers.tripStateManager.isCurrentState("BANK_BARS") == true }
        sequence {
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
            sipStaminaPotion(logger, managers.staminaManager, managers.playerRunManager)
            condition {
                managers.tripStateManager.cycleStateFrom(
                    managers.tripStateManager.getCurrentKey()
                )
            }
            perform {
                Lottery.execute(1.0) {
                    managers.cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    selector {
        condition { managers.tripStateManager.isCurrentState("COLLECT_BARS") == true }
        sequence {
            collectBarsNode(
                logger,
                managers.dispenserManager,
                managers.tripStateManager,
                managers.repetitiveActionManager
            )
            perform {
                Lottery.execute(0.6) {
                    managers.cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    /**
     * Given we are processing base
     * And the dispenser is not holding bars
     * And our inventory is full
     *
     * We will withdraw base ores
     */
    selector {
        condition { managers.tripStateManager.isCurrentState("PROCESS_BASE") == true }
        condition { managers.dispenserManager.holdsBars() }
        condition { Inventory.isFull() }
        sequence {
            ensureIsOpenNode(logger)
            bankNode(logger, true, false)
            sipStaminaPotion(logger, managers.staminaManager, managers.playerRunManager)
            withdrawItemNode(
                logger,
                managers.tripStateManager.baseOre.name(),
                managers.tripStateManager.baseOre.quantity(),
                true
            )
            perform {
                Lottery.execute(0.6) {
                    managers.cameraManager.randomize(zoom = false)
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
        condition { managers.tripStateManager.isCurrentState("PROCESS_BASE") == true }
        condition { managers.dispenserManager.holdsBars() }
        condition { Inventory.isEmpty() }
        sequence {
            loadOresNode(logger, managers.repetitiveActionManager)
            condition {
                managers.tripStateManager.cycleStateFrom(
                    managers.tripStateManager.getCurrentKey()
                )
            }
            perform {
                if (Settings.preWalkChecked) {
                    val preWalkTile = WorldTile(1939, 4963, 0)
                    LocalWalking.walkTo(preWalkTile)
                }

                Lottery.execute(0.78) {
                    managers.cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    /**
     * Some bars do not use secondary materials. So for that, we do not always want to include this sequence
     */
    if (managers.tripStateManager.states.containsKey("PROCESS_SECONDARY")) {
        /**
         *  Given we are tasked to process coal
         *  And the melting pot contains more than our threshold in terms of coal stock
         *  We simply skip the coal task, to prevent any overfilling
         */
        selector {
            condition { managers.tripStateManager.isCurrentState("PROCESS_SECONDARY") == true }
            condition { !managers.meltingPotManager.containsCoalMoreThan(112) }
            condition {
                managers.tripStateManager.cycleStateFrom(
                    managers.tripStateManager.getCurrentKey()
                )
            }
        }

        /**
         * Given we are processing coal
         * And the dispenser is not holding bars
         * And our inventory is empty
         *
         * We will withdraw secondary ores
         */
        selector {
            condition { managers.tripStateManager.isCurrentState("PROCESS_SECONDARY") == true }
            condition { managers.dispenserManager.holdsBars() }
            condition { Inventory.isFull() }
            sequence {
                ensureIsOpenNode(logger)
                bankNode(logger, true, false)
                sipStaminaPotion(logger, managers.staminaManager, managers.playerRunManager)

                withdrawItemNode(
                    logger,
                    managers.tripStateManager.secondaryOre!!.name(),
                    managers.tripStateManager.secondaryOre.quantity(),
                    true
                )
                perform {
                    Lottery.execute(0.6) {
                        managers.cameraManager.randomize(zoom = false)
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
            condition { managers.tripStateManager.isCurrentState("PROCESS_SECONDARY") == true }
            condition { managers.dispenserManager.holdsBars() }
            condition { Inventory.isEmpty() }
            sequence {
                loadOresNode(logger, managers.repetitiveActionManager)
                condition {
                    managers.tripStateManager.cycleStateFrom(
                        managers.tripStateManager.getCurrentKey()
                    )
                }
                perform {
                    Lottery.execute(0.6) {
                        managers.cameraManager.randomize(zoom = false)
                    }
                }
            }
        }
    }
}