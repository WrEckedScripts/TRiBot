package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger

//Todo, refactor usage, since we dropped all of the custom code
// Any implementation for refactoring, does need to be refactored to a condition { Bank.ensureOpen }
fun IParentNode.ensureIsOpenNode(logger: Logger) = sequence {
    selector {
        condition {
            Bank.ensureOpen()
        }
    }
}