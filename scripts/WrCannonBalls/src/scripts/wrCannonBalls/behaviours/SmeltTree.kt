package scripts.wrCannonBalls.behaviours

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrCannonBalls.behaviours.banking.CannonballInventory
import scripts.wrCannonBalls.behaviours.banking.ensureSmeltReadyInventory
import scripts.wrCannonBalls.behaviours.smelting.InteractFurnace
import scripts.wrCannonBalls.behaviours.smelting.isSmeltingBalls
import scripts.wrCannonBalls.managers.Container

fun getSmeltTree(
    logger: Logger,
    managers: Container
) = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        sequence {
            /**
             * Ensures that we're logged in, after we get disconnected for example.
             */
            selector {
                condition { Login.isLoggedIn() }
                condition {
                    logger.debug("login condition")
                    Login.login()
                }
            }

            // Failsafe to anytime a makeScreen is open, we simply click makeAll
            selector {
                condition { !MakeScreen.isOpen() }
                condition {
                    Waiting.wait(FatigueResolver.getMilliseconds())
                    MakeScreen.makeAll("Cannonball")
                }
            }

            selector {
                condition { isSmeltingBalls(logger, managers) }
                condition { CannonballInventory().ready() }
                sequence {
                    // Walk to bank.
                    condition {
                        Bank.ensureOpen()
                    }
                    // Ensures we're holding a valid inventory, ready for smelting.
                    ensureSmeltReadyInventory(logger, managers)
                    condition {
                        InteractFurnace(logger, managers).execute()
                    }
                    condition {
                        isSmeltingBalls(logger, managers)
                    }
                }
            }
        }
    }
}