package scripts.wrCannonBalls.behaviours

import org.tribot.script.sdk.*
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.calculators.ResourceCounter
import scripts.utils.progress.webhook.DiscordNotifier
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
                    Login.login()
                }
            }

            selector {
                condition { !MakeScreen.isOpen() }
                condition {
                    logger.warn("Found open makeScreen, time to re-init cannonballs smelting")
                    Waiting.wait(FatigueResolver.getMilliseconds())
                    MakeScreen.makeAll("Cannonball")
                }
            }

            selector {
                perform {
                    DiscordNotifier.notify(
                        message = "${MyPlayer.getUsername()} is currently at: ${
                            ResourceCounter.getPaintLabelFor(
                                "Cannonball"
                            )
                        }"
                    )
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