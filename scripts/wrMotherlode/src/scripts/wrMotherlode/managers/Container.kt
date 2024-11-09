package scripts.wrMotherlode.managers

import scripts.utils.failsafes.RepetitiveActionManager

data class Container(
    val repetitiveActionManager: RepetitiveActionManager,
    val progressionManager: ProgressionManager,
    val stateManager: StateManager,
    val sackManager: SackManager
)