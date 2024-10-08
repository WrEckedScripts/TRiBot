package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Equipment
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

/**
 * Sequence to ensure that the player is wearing their ice gloves.
 * If no ice gloves are worn, this sequence should be executed
 * Within this Sequence, we first check, if our inventory holds a pair of ice gloves
 * - Happy flow (true scenario)
 * - - We then simply press "Wear" and make sure the player is wearing them.
 * - Un-happy flow (false scenario)
 * - - We move to the bank
 * - - Withdraw a pair of Ice gloves
 * - - And wear them
 *
 * If both cases fail, the script will be terminated.
 */
fun IParentNode.ensureIceGlovesAreWorn(logger: Logger) = sequence {
    // In between world hops etc, this prevents early exit-ing the script
    Waiting.waitNormal(650, 40)

    val presentInInventory = Query.inventory()
        .nameEquals("Ice gloves")
        .findFirst()
        .isPresent

    if (!presentInInventory) {
        // If we do not have Ice gloves worn, or in our inventory, open the bank and try to withdraw 1.
        ensureIsOpenNode(logger)
        withdrawItemNode(logger, "Ice gloves", 1, false)
    }

    condition {
        Waiting.waitUntil(3000) {
            Query.inventory()
                .nameEquals("Ice gloves")
                .findFirst()
                .map { it.click("Wear") }

            // Slight wait, to prevent spam checking
            Waiting.waitNormal(300, 50)

            // Returns success/fail to the Waiting.
            Equipment.contains("Ice gloves")
        }
    }
}