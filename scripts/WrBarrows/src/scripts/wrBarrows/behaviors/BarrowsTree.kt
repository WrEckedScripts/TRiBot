package scripts.wrBarrows.behaviors

import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrBarrows.behaviors.combat.combatSequence
import scripts.wrBarrows.behaviors.requirements.ensureIsMember
import scripts.wrBarrows.behaviors.requirements.ensureLoggedIn
import scripts.wrBarrows.behaviors.rooms.roomSequence
import scripts.wrBarrows.behaviors.tunnel.lootingSequence
import scripts.wrBarrows.behaviors.tunnel.tunnelSequence
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State
import scripts.wrBarrows.player.BarrowsArea

fun barrowsTree(
    logger: Logger,
    managers: Container
) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        sequence {
            ensureLoggedIn()
            //TODO ensure safely handling exceptions!
            ensureIsMember()

            // TODO failsafes
            // - logout upon sent to lumbridge / listen for oh dear you died message etc.

//            selector {
//                condition { managers.stateManager.isCurrentState(State.PREPARE.name) }
//                //TODO prepare sequence
//                preparationSequence(managers)
//            }

            selector {
                condition {
                    !BarrowsArea.BARROWS.surface.containsMyPlayer() && managers.stateManager.isCurrentState(State.ROOM.name)
                }
                condition {
                    managers.stateManager.set(State.ROOM.name)
                }
            }

            selector {
                condition {
                    !BarrowsArea.BARROWS.crypt.containsMyPlayer() && managers.stateManager.isCurrentState(State.TUNNEL.name)
                }
                condition {
                    managers.stateManager.set(State.TUNNEL.name)
                }
            }

            selector {
                condition {
                    !managers.tunnelManager.insideChestRoom() && managers.stateManager.isCurrentState(State.LOOT.name)
                }
                condition {
                    managers.stateManager.set(State.LOOT.name)
                }
            }

            selector {
                condition { managers.stateManager.isCurrentState(State.ROOM.name) }
                roomSequence(managers)
            }

            selector {
                condition { managers.stateManager.isCurrentState(State.FIGHT.name) }
                combatSequence(managers)
                //If this was the second last brother and we do not know the tunnel (we can now assume)
                // or if this was the second last and we know the tunnel
                // set the state to room, and let's move towards the tunnel
                // inside the tunnel we still need to fight the remaining brother
            }

            selector {
                condition { managers.stateManager.isCurrentState(State.TUNNEL.name) }
                tunnelSequence(managers)
            }

            selector {
                condition { managers.stateManager.isCurrentState(State.LOOT.name) }
                lootingSequence(managers)
            }

            // We need to have a state manager, to prevent looping back
            // State for Preparing Trip
            // State for Killing brothers
            // State for navigating tunnel
            // - do note, a brother can spawn
            // State for looting chest
            // - do note, a brother can spawn
            // - after looting, either go another trip or prepare (at bank)

            //TODO preparing inventory/gear

            //TODO moving to barrows via teletab

            //TODO if inside area, start new trip, dig through the crypts

            //TODO inside crypt find out if tunnel or brother

            //TODO if tunnel mark and continue others if unkilled remain

            //TODO if brother, start killing task
            // - PrayerBrotherKiller
            // - - Define per brother which prayer
            // - FoodBrotherKiller
        }
    }
}
