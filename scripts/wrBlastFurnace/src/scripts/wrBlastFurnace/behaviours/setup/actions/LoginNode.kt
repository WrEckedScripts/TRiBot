package scripts.wrBlastFurnace.behaviours.setup.actions

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger

fun IParentNode.loginNode(logger: Logger) = sequence {
    condition { !Login.isLoggedIn() }
    perform {
        logger.debug(!Login.isLoggedIn())
        Login.login()
    }
}