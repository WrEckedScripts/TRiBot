package scripts.wrSulphurNagua.managers

import scripts.utils.failsafes.RepetitiveActionManager
import scripts.wrSulphurNagua.managers.states.StateManager

data class Container(
    val repetitiveActionManager: RepetitiveActionManager,
    val combatManager: CombatManager,
    val stateManager: StateManager
)