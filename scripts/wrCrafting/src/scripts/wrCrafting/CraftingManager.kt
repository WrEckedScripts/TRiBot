package scripts.wrCrafting

import org.tribot.script.sdk.Bank
import org.tribot.script.sdk.BankSettings
import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrCrafting.models.TaskConfiguration

interface CraftingManagerInterface {
    val taskConfiguration: TaskConfiguration

    fun logger(): Logger

    fun initCrafting(): Boolean
}

class CraftingManager(
    override val taskConfiguration: TaskConfiguration

) : CraftingManagerInterface {
    override fun logger(): Logger {
        return Logger("Crafting")
    }

    override fun initCrafting(): Boolean {
        logger().info("Initialized crafting")

        //TODO verify if all widgets are closed
        // TODO make sure bank is closed

        if (Bank.isOpen()) {
            Bank.close()
        }

        val chisel = Query.inventory()
            .nameContains("Chisel")
            .findFirst()

        val craftableItem = Query.inventory()
            .nameEquals(taskConfiguration.craftableName)
            .findFirst()

        Waiting.waitUntil {
            chisel.get().useOn(craftableItem.get())
        }

        Waiting.waitUntil {
            MakeScreen.isOpen()
            MakeScreen.makeAll(taskConfiguration.craftableName)
        }

        Waiting.waitUntil {
            MyPlayer.isAnimating()
        }

        taskConfiguration.isProcessing = true

        logger().debug("TASK set to processing state")

        return true
    }

    //todo
    //check following code regards if the player is still crafting
    //src: https://discord.com/channels/438569333233287170/770374621312450560/1258586312894578709
    /**
     * Returns true if a break condition is successfully met, false otherwise.
     */
//    fun idleWhileAnimating(
//        vararg breakConditions: BooleanSupplier,
//        timeout: Int = Random.nextInt(2500, 3500)
//    ): Boolean {
//        var lastAnimation = System.currentTimeMillis()
//        var rollingTime = System.currentTimeMillis()
//
//        while ((rollingTime - lastAnimation) < timeout) {
//            if (Arrays.stream(breakConditions).anyMatch { it.asBoolean }) {
//                return true
//            }
//
//            if (MyPlayer.isAnimating() or MyPlayer.isMoving()) {
//                lastAnimation = System.currentTimeMillis()
//            }
//
//            rollingTime = System.currentTimeMillis()
//
//            Waiting.waitNormal(50, 10)
//        }
//
//        return false
//    }
}