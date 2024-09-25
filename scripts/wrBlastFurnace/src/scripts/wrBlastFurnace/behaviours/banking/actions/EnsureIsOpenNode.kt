package scripts.wrBlastFurnace.behaviours.banking.actions

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import org.tribot.script.sdk.walking.GlobalWalking
import scripts.utils.Logger

fun IParentNode.ensureIsOpenNode(logger: Logger) = sequence {
    selector {
        // If the bank is not open
        condition {
            Bank.isOpen()
        }
        // Walk to the nearest bank, when there's a bank nearby
        sequence {
            selector {
                condition {
                    Bank.isNearby()
                }
                condition {
                    GlobalWalking.walkToBank()
                }
            }
            condition {
                Bank.ensureOpen()
            }
        }
    }
}