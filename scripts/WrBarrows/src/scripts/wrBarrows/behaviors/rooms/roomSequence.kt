package scripts.wrBarrows.behaviors.rooms

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.frameworks.behaviortree.IParentNode
import org.tribot.script.sdk.frameworks.behaviortree.condition
import org.tribot.script.sdk.frameworks.behaviortree.selector
import org.tribot.script.sdk.frameworks.behaviortree.sequence
import scripts.wrBarrows.behaviors.rooms.tasks.DigToRoom
import scripts.wrBarrows.behaviors.rooms.tasks.InteractSarcophagus
import scripts.wrBarrows.behaviors.rooms.tasks.WalkToDigsite
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.player.BarrowsArea

fun IParentNode.roomSequence(managers: Container) = sequence {
    //TODO implement selectors for each scenario to act accordingly

    selector {
        condition { WalkToDigsite(managers).satisfied() }
        condition {
            WalkToDigsite(managers).execute()
        }
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
        condition { InteractSarcophagus(managers).satisfied() }
        condition { BarrowsArea.BARROWS.surface.containsMyPlayer() }
        condition { MyPlayer.get().get().isInteracting }
        condition {
            InteractSarcophagus(managers).execute()
        }
    }
}