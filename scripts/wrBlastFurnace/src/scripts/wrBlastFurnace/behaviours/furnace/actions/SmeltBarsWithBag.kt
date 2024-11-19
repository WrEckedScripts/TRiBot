package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.behaviours.banking.actions.bankNode
import scripts.wrBlastFurnace.behaviours.banking.actions.emptyCoalBag
import scripts.wrBlastFurnace.behaviours.banking.actions.ensureIsOpenNode
import scripts.wrBlastFurnace.behaviours.banking.actions.fillCoalBag
import scripts.wrBlastFurnace.behaviours.banking.actions.prepareInventoryNode
import scripts.wrBlastFurnace.behaviours.furnace.failsafes.SmithingArea
import scripts.wrBlastFurnace.behaviours.stamina.actions.sipStaminaPotion
import scripts.wrBlastFurnace.gui.Settings
import scripts.wrBlastFurnace.managers.Container
import kotlin.random.Random

fun IParentNode.smeltBarsWithBag(
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
     * Bank the bars
     */
    selector {
        condition { managers.tripStateManager.isCurrentState("BANK_BARS") }
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

    /**
     * Bar collection
     */
    selector {
        condition { managers.tripStateManager.isCurrentState("COLLECT_BARS") }
        sequence {
            collectBarsNode(
                logger,
                managers.dispenserManager,
                managers.tripStateManager,
                managers.repetitiveActionManager
            )

            perform {
                Lottery.execute(Random.nextDouble(0.62, 0.84)) {
                    managers.cameraManager.randomize(zoom = false)
                }
            }
        }
    }

    /**
     * Skipping pre-fill
     */
    selector {
        condition { managers.tripStateManager.isCurrentState("PREFILL_COAL") }
        condition { !managers.meltingPotManager.containsCoalMoreThan(112) }
        condition {
            logger.error(
                "" +
                        "STATE: ---- ${managers.tripStateManager.isCurrentState("PREFILL_COAL")}" +
                        "MELTP: ---- ${managers.meltingPotManager.containsCoalMoreThan(112)}" +
                        "INVERSE:--- ${!managers.meltingPotManager.containsCoalMoreThan(112)}"
            )
            logger.error("Skipping, we got: ${managers.meltingPotManager.getCoalCount()}")
            managers.tripStateManager.cycleStateFrom(
                managers.tripStateManager.getCurrentKey()
            )
        }
    }

    /**
     * Prefilling
     */
    selector {
        condition {
            managers.tripStateManager.isCurrentState("PREFILL_COAL")
        }
        sequence {
            ensureIsOpenNode(logger)

//            selector {
//                condition {
//                    Inventory.contains("Coal bag")
//                }
//                withdrawItemNode(
//                    logger,
//                    "Coal bag",
//                    1,
//                    false
//                )
//            }

            fillCoalBag(logger, managers)

            //TODO NEW - untested
            prepareInventoryNode(
                logger,
                managers.tripStateManager.secondaryOre!!
            )

            //OLD
//            withdrawItemNode(
//                logger,
//                managers.tripStateManager.secondaryOre!!.name,
//                managers.tripStateManager.secondaryOre.quantity,
//            )

            // load our inventory coal
            loadOresNode(
                logger,
                managers.repetitiveActionManager
            )
            // Empty coal bag
            emptyCoalBag(logger)
            // load our coal bag inventory
            loadOresNode(
                logger,
                managers.repetitiveActionManager
            )

            // Cycle state
            condition {
                managers.tripStateManager.cycleStateFrom(
                    managers.tripStateManager.getCurrentKey()
                )
            }
        }
    }

    /**
     * Grabbing a full coal bag + iron ores
     */
    selector {
        condition { managers.tripStateManager.isCurrentState("PREPARE_ORES") }
        condition { managers.dispenserManager.holdsBars() }
        condition { Inventory.isFull() }
        sequence {
            ensureIsOpenNode(logger)

//            selector {
//                condition {
//                    Inventory.contains("Coal bag")
//                }
//                withdrawItemNode(
//                    logger,
//                    "Coal bag",
//                    1,
//                    false
//                )
//            }

            sipStaminaPotion(
                logger,
                managers.staminaManager,
                managers.playerRunManager
            )

            fillCoalBag(logger, managers)

            //TODO - NEW UNTESTED
            prepareInventoryNode(
                logger,
                managers.tripStateManager.baseOre
            )

            //OLD
//            withdrawItemNode(
//                logger,
//                managers.tripStateManager.baseOre.name,
//                managers.tripStateManager.baseOre.quantity
//            )

            condition {
                managers.tripStateManager.cycleStateFrom(
                    managers.tripStateManager.getCurrentKey()
                )
            }

            //TODO Lottery.execute (0.6) camera randomize
        }
    }

    /**
     * Let's load them
     */
    selector {
        condition { managers.tripStateManager.isCurrentState("PROCESS_ORES") }
        sequence {
            condition { !managers.dispenserManager.holdsBars() }

            // Put Iron ores
            loadOresNode(
                logger,
                managers.repetitiveActionManager
            )

            emptyCoalBag(logger)

            // Put Coal ores (from coal bag)
            loadOresNode(
                logger,
                managers.repetitiveActionManager
            )

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
}