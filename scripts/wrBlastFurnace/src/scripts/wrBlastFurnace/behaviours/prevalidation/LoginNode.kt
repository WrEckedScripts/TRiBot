package scripts.wrBlastFurnace.behaviours.prevalidation

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger

fun IParentNode.loginNode(logger: Logger) = sequence {
    condition { !Login.isLoggedIn() }
    perform {
        logger.info("Awaiting login handler to login the account")
        Login.login()
    }
}