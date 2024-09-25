package scripts.wrCrafting

import org.tribot.script.sdk.Inventory
import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger

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
}