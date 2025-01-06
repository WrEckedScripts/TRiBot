package scripts.wrBarrows.behaviors.rooms

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.utils.Logger
import scripts.wrBarrows.behaviors.rooms.tasks.DigToRoom
import scripts.wrBarrows.behaviors.rooms.tasks.InteractSarcophagus
import scripts.wrBarrows.behaviors.rooms.tasks.LeaveRoom
import scripts.wrBarrows.behaviors.rooms.tasks.WalkToDigsite
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State
import scripts.wrBarrows.player.BarrowsArea

fun IParentNode.roomSequence(managers: Container) = sequence {
    //TODO implement selectors for each scenario to act accordingly

    // TODO add selector that if we're being attacked, we move to the FIGHT state

    // If we're inside the wrong room, time to leave and head to the next.
    selector {
        condition { BarrowsArea.BARROWS.surface.containsMyPlayer() }
        condition {
            Logger("CompareCrypts").error("current: ${managers.roomManager.getCurrentCrypt()?.value?.room?.name}")
            Logger("CompareCrypts").error("target: ${managers.roomManager.getTargetCrypt().room.name}")
            managers.roomManager.getCurrentCrypt()?.value == managers.roomManager.getTargetCrypt()
        }
        condition {
            Logger("LeaveRoomCondition").debug("Executing leaveRoom.")
            LeaveRoom(managers).execute()
        }
    }

    selector {
        condition { BarrowsArea.BARROWS.surface.containsMyPlayer() }
        condition { InteractSarcophagus(managers).satisfied() }
        condition {
            Logger("roomSequence").warn("MyPlayer: ${MyPlayer.get().get().isHealthBarVisible}")
            MyPlayer.get().get().isHealthBarVisible
        }
        condition {
            Logger("roomSequence").warn("Exec Interact")
            InteractSarcophagus(managers).execute()
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
        condition {
            DigToRoom(managers).satisfied()
        }
        condition {
            DigToRoom(managers).execute()
        }
    }

    selector {
        condition { WalkToDigsite(managers).satisfied() }
        condition {
            WalkToDigsite(managers).execute()
        }
    }
}