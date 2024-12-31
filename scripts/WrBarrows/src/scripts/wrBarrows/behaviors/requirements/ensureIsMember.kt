package scripts.wrBarrows.behaviors.requirements

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector

fun IParentNode.ensureIsMember() {
    selector {
        condition { MyPlayer.isMember() }
        condition {
            throw Exception("Ran out of membership..")
        }
    }
}