package scripts.wrBarrows.behaviors.tunnel

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.behaviors.tunnel.tasks.pathing.LockPickDoors
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State

fun IParentNode.tunnelSequence(managers: Container) = sequence {

    selector {
        condition { managers.tunnelManager.hasSpawn() }
        condition {
            managers.tunnelManager.init()

            managers.tunnelManager.hasSpawn()
        }
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
        condition { LockPickDoors(managers).satisfied() }
        condition { LockPickDoors(managers).execute() }
    }
}