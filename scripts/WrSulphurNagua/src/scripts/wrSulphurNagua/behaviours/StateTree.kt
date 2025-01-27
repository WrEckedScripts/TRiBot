package scripts.wrSulphurNagua.behaviours

import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.playerstate.nodes.ensureIsMember
import scripts.utils.playerstate.nodes.ensureLoggedIn
import scripts.wrSulphurNagua.behaviours.preparation.preparationSequence
import scripts.wrSulphurNagua.managers.Container
import scripts.wrSulphurNagua.managers.states.State

fun stateTree(
    managers: Container
) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        sequence {
            ensureLoggedIn()
            ensureIsMember()

            // Main states and their respective sequences
            // Each of these trees can and probably will have their own subtree's / substates
            selector {
                condition { managers.stateManager.isState(State.PREPARATION.name) }
                preparationSequence()
            }
        }
    }
}