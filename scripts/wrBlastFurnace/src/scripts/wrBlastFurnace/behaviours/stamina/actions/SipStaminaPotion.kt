package scripts.wrBlastFurnace.behaviours.stamina.actions

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.utils.antiban.Lottery
import scripts.utils.behaviours.banking.actions.withdrawItemNode
import scripts.wrBlastFurnace.managers.PlayerRunManager
import scripts.wrBlastFurnace.managers.StaminaManager

/**
 * We expect the bank to be open when running this.
 */
fun IParentNode.sipStaminaPotion(
    logger: Logger,
    staminaManager: StaminaManager,
    playerRunManager: PlayerRunManager
) = sequence {
    selector {
        condition { staminaManager.satisfiesStaminaState() }
        condition { staminaManager.notOutOfPotions() }
        sequence {
            withdrawItemNode(logger, "Stamina potion", 1, false)
            perform {
                Waiting.waitUntil {
                    Waiting.waitNormal(500, 24)
                    val sipped = staminaManager.sipStamina()

                    // 75% of the time, we will enable run directly after sipping.
                    // The remaining 25% will be handled through the tree, where we do enable run if we need to
                    Lottery.execute(0.75) {
                        playerRunManager.enableRun()
                    }

                    sipped
                }
            }
        }

    }
}