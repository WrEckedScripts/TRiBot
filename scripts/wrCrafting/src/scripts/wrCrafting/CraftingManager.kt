package scripts.wrCrafting

import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import java.util.*
import java.util.function.BooleanSupplier
import kotlin.random.Random

interface CraftingManagerInterface {
    val logger: Logger
    val craftableName: String

    fun initCrafting(): Boolean
}

class CraftingManager(
    override val logger: Logger,
    override val craftableName: String
) : CraftingManagerInterface {
    // Marker to indicate we're processing a batch.
    var isProcessing: Boolean = false

    override fun initCrafting(): Boolean {
        logger.debug("[INIT] - start func")

        val chisel = Query.inventory()
            .nameContains("Chisel")
            .findFirst()

        val craftableItem = Query.inventory()
            .nameEquals(craftableName)
            .findFirst()

        Waiting.waitUntil {
            chisel.get().useOn(craftableItem.get())
        }

        Waiting.waitUntil {
            MakeScreen.isOpen()
            MakeScreen.makeAll(craftableName)
        }

        Waiting.waitUntil {
            MyPlayer.isAnimating()
        }

        isProcessing = true

        logger.debug("[INIT] - end of func")

        return true
    }

    //todo
    //check following code regards if the player is still crafting
    //src: https://discord.com/channels/438569333233287170/770374621312450560/1258586312894578709
    /**
     * Returns true if a break condition is successfully met, false otherwise.
     */
    fun idleWhileAnimating(
        vararg breakConditions: BooleanSupplier,
        timeout: Int = Random.nextInt(2500, 3500)
    ): Boolean {
        var lastAnimation = System.currentTimeMillis()
        var rollingTime = System.currentTimeMillis()

        while ((rollingTime - lastAnimation) < timeout) {
            if (Arrays.stream(breakConditions).anyMatch { it.asBoolean }) {
                return true
            }

            if (MyPlayer.isAnimating() or MyPlayer.isMoving()) {
                lastAnimation = System.currentTimeMillis()
            }

            rollingTime = System.currentTimeMillis()

            Waiting.waitNormal(50, 10)
        }

        return false
    }
}