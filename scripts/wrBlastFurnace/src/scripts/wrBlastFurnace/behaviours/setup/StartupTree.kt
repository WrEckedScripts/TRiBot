package scripts.wrBlastFurnace.behaviours.setup

import org.tribot.script.sdk.frameworks.behaviortree.BehaviorTreeStatus
import org.tribot.script.sdk.frameworks.behaviortree.behaviorTree
import org.tribot.script.sdk.frameworks.behaviortree.repeatUntil
import org.tribot.script.sdk.frameworks.behaviortree.selector
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.setup.actions.loginNode

fun getStartupTree(logger: Logger) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.SUCCESS) {
        selector {
            loginNode(logger)
        }
    }
}