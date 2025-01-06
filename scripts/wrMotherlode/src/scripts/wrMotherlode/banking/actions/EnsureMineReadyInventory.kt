package scripts.wrMotherlode.banking.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrMotherlode.managers.Container

fun IParentNode.ensureMineReadyInventory(
    logger: Logger,
    managers: Container
) = sequence {
    selector {
        sequence {
            condition {
                // Only if the sack is full.
                if (!managers.sackManager.canBeFilled()) {
                    Waiting.waitUntil {
                        logger.debug("We are going to drop our inventory and start collecting")
                        Query.inventory()
                            .nameEquals("Pay-dirt")
                            .forEach {
                                it.click("Drop")
                            }

                        Waiting.wait(FatigueResolver.getMilliseconds() * 2)

                        if (!Inventory.contains("Pay-dirt")) {
                            managers.stateManager.resetCycle("COLLECTING")
                        }

                        return@waitUntil false
                    }
                }

                val bankTask = MineReadyInventoryBuilder().task()

                Waiting.waitUntil(30_000, FatigueResolver.getMilliseconds() * 4) {
                    if (!bankTask.isSatisfied()) {
                        logger.error("Our inventory is not satisfied, executing bankTask")
                        bankTask.execute()
                    }

                    bankTask.isSatisfied()
                }
            }
        }
    }
}