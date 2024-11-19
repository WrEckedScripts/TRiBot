package scripts.wrMotherlode.banking.actions

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import scripts.utils.Logger
import scripts.wrMotherlode.managers.Container

fun IParentNode.ensureMineReadyInventory(
    logger: Logger,
    managers: Container
) = sequence {
    selector {
        sequence {
            condition {
                // Only if the sack is full.
                if (!managers.sackManager.needsFilling()) {
                    Waiting.waitUntil {
                        logger.debug("We are going to drop our inventory and start collecting")
                        Query.inventory()
                            .nameEquals("Pay-dirt")
                            .forEach {
                                it.click("Drop")
                            }

                        if (!Inventory.contains("Pay-dirt")) {
                            managers.stateManager.resetCycle("COLLECTING")
                        }

                        return@waitUntil false
                    }
                }

                val bankTask = BankTask.builder()
//                    .addInvItem(1275, Amount.of(1)) // Rune Pickaxe
                    .addInvItem(11920, Amount.of(1)) // Dragon Pickaxe
                    .addInvItem(2347, Amount.of(1)) // Hammer
                    // Pay-dirt, although not bankable, we should accept them, this avoids trying to bank them.
                    .addInvItem(12011, Amount.range(0, 26))
                    .build()

                if (!bankTask.isSatisfied()) {
                    logger.error("Our inventory is not satisfied, executing bankTask")
                    bankTask.execute()
                }

                Waiting.waitUntil(30_000) {
                    bankTask.isSatisfied()
                }
            }
        }
    }
}