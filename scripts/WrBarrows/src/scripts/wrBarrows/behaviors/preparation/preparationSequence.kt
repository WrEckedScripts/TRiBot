package scripts.wrBarrows.behaviors.preparation

import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.managers.Container

fun IParentNode.preparationSequence(managers: Container) = sequence {
    //either prepare a new inventory and move to barrows
    // or, if we got enough supplies, we can simply re-init barrows teleport.
}