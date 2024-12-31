package scripts.wrBarrows.behaviors.combat

import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.managers.Container

fun IParentNode.combatSequence(managers: Container) = sequence {
    //TODO implement selectors for each scenario to act accordingly

    // Heal in time
    // Sip boosting potion
    // Sip prayer potion, if fighting prayer brother
    // Re-attack if not attacking
    // Set state once brother is killed to move to the next brother
    // Do note, it's possible that we do initiate fighting with a tunnel npc (non brother)

    // Failsafe that when we're out of food / prayer / prayer pots when needing them
    // - We should teleport out or exit the room..
}