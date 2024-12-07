package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBlastFurnace.banking.materials.Ore
import scripts.wrBlastFurnace.gui.Settings
import scripts.wrBlastFurnace.managers.Container

/**
 * Node that ensures we have a inventory prepared for our next ore trip
 * --TODO ensure proper validation/testing is done! both old/new smelts
 */
fun IParentNode.prepareInventoryNode(
    logger: Logger,
    ore: Ore,
    managers: Container
) = sequence {
    condition {
        // We always prep our inventory after we fill our coalbag, so let's reset it here.
        managers.repetitiveActionManager.reset("fill-coalbag")

        managers.repetitiveActionManager.increment("prepare-inventory", 3)
        val usingCoalBag = Settings.coalBagChecked

        val task = BankTask.builder()
        if (usingCoalBag) {
            task.addInvItem(12019, Amount.of(1))
        }

        task.addInvItem(
            ore.id(),
            Amount.of(ore.quantity())
        )

        val builtTask = task.build()

        if (!builtTask.isSatisfied()) {
            builtTask.execute()
        }

        val satisfied = Waiting.waitUntil(FatigueResolver.getMilliseconds() * 6) {
            Waiting.wait(FatigueResolver.getMilliseconds())
            builtTask.isSatisfied()
        }

        if (satisfied) {
            managers.repetitiveActionManager.reset("prepare-inventory")
        }

        satisfied
    }
}