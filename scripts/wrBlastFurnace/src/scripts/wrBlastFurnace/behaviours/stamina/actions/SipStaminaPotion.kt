package scripts.wrBlastFurnace.behaviours.stamina.actions

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.banking.actions.withdrawItemNode
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
            // needs a explicit name now, won't work with pots..?
            withdrawItemNode(logger, "Stamina potion", 1, false)
            perform {
                Waiting.waitUntil {
                    Waiting.waitNormal(500, 24)
                    staminaManager.sipStamina()
                    playerRunManager.enableRun()
                }
            }
        }

    }
}