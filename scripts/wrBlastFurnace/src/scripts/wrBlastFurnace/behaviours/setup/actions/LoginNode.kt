package scripts.wrBlastFurnace.behaviours.setup.actions

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import scripts.utils.Logger

fun IParentNode.loginNode(logger: Logger) = selector {
    condition { Login.isLoggedIn() }
    condition {
        Login.login()
    }
}