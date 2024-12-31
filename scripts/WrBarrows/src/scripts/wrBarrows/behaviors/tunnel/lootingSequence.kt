package scripts.wrBarrows.behaviors.tunnel

import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.managers.Container

fun IParentNode.lootingSequence(managers: Container) = sequence {
    //TODO implement selectors for each scenario to act accordingly

    // Get extra potential if necessary
    // handle the looting screen - trigger profit calculations
    // If we haven't seen the last brother yet, make sure we handle it correctly
    // teleport back to barrows or bank for preparing.
}