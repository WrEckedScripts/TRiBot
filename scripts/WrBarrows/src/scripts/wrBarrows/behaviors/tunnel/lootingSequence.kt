package scripts.wrBarrows.behaviors.tunnel

import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.behaviors.combat.tasks.CheckIAmUnderAttack
import scripts.wrBarrows.behaviors.tunnel.tasks.looting.LootChest
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State

fun IParentNode.lootingSequence(managers: Container) = sequence {
    //TODO implement selectors for each scenario to act accordingly

    selector {
        condition { LootChest(managers).satisfied() }
        condition { LootChest(managers).execute() }
    }

    selector {
        condition { CheckIAmUnderAttack(managers).execute() }
        condition { managers.stateManager.set(State.FIGHT.name) }
    }

    // Get extra potential if necessary
    // handle the looting screen - trigger profit calculations
    // If we haven't seen the last brother yet, make sure we handle it correctly
    // teleport back to barrows or bank for preparing.
}