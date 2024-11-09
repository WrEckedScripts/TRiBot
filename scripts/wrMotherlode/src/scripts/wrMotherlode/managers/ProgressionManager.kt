package scripts.wrMotherlode.managers

import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrMotherlode.overlay.ResourceCounter

class ProgressionManager(
    private val logger: Logger,
    private val startedAt: Long
) {

    fun registerLoot() {
        Query.inventory()
            .forEach {
                ResourceCounter.increment(it.name, it.stack)
            }

        logger.info("Updated loot")
    }
}