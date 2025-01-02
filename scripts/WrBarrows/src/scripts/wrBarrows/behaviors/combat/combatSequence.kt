package scripts.wrBarrows.behaviors.combat

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.behaviors.combat.tasks.AttackBrother
import scripts.wrBarrows.behaviors.combat.tasks.ConsumeFood
import scripts.wrBarrows.behaviors.combat.tasks.ConsumePotion
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State

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

    selector {
        condition { ConsumePotion("Prayer potion").satisfied() }
        condition { ConsumePotion("Prayer potion").execute() }
    }

    selector {
        condition { ConsumeFood().should() }
        condition { ConsumeFood().execute() }
    }

    selector {
        condition { AttackBrother(managers).satisfied() }
        condition { AttackBrother(managers).isInCombat() }
        condition { AttackBrother(managers).execute() }
    }

    selector {
        condition {
            // If not in combat and am in the wrong room, mark this room as complete and move along
            // like we do within the AttackBrother class.
            Waiting.waitUntil(5_000, 750) {
                Waiting.wait(3_000)
                AttackBrother(managers).isInCombat()
            }
        }
        condition {
            managers.roomManager.markAsCompleted(
                managers.roomManager.getCurrentCrypt()!!.value.room
            )
            managers.stateManager.set(State.ROOM.name)
        }
    }
}