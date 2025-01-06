package scripts.wrBarrows.behaviors.tunnel

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.behaviors.tunnel.tasks.looting.OpenChest
import scripts.wrBarrows.behaviors.tunnel.tasks.looting.SearchChest
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State

fun IParentNode.lootingSequence(managers: Container) = sequence {
    selector {
        condition { OpenChest(managers).satisfied() }
        condition { OpenChest(managers).execute() }
    }

    selector {
        condition {
            Waiting.waitUntil(5_000) {
                !managers.combatManager.targetBrotherIsSpawned()
            }
        }
        condition { managers.stateManager.set(State.FIGHT.name) }
    }

    selector {
        condition { SearchChest(managers).satisfied() }
        condition { SearchChest(managers).execute() }
    }

    //TODO leave room selector (teleport)
}