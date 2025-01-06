package scripts.wrBarrows.managers

import scripts.utils.failsafes.RepetitiveActionManager
import scripts.wrBarrows.rooms.RoomManager
import scripts.wrBarrows.tunnel.TunnelManager

data class Container(
    val repetitiveActionManager: RepetitiveActionManager,
    val roomManager: RoomManager,
    val stateManager: StateManager,
    val tunnelManager: TunnelManager,
    val combatManager: CombatManager
)
