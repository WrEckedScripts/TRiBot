package scripts.utils.playerstate.nodes

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector

fun IParentNode.ensureLoggedIn() = selector {
    selector {
        condition { Login.isLoggedIn() }
        condition { Login.login() }
    }
}