package scripts.wrBarrows.behaviors.requirements

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.perform
import org.tribot.script.sdk.frameworks.behaviortree.selector

fun IParentNode.ensureIsMember() {
    selector {
        condition { Login.isLoggedIn() }
        condition {
            Login.login()
        }
    }

    selector {
        condition { !Login.isLoggedIn() }
        perform {
            if (!MyPlayer.isMember()) {
                throw Exception("Ran out of membership..")
            }
        }
    }
}