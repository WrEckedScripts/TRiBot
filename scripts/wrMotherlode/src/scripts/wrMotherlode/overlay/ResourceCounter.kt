package scripts.wrMotherlode.overlay

import org.tribot.script.sdk.pricing.Pricing
import scripts.utils.formatters.Notator

object ResourceCounter {
    // Initialize ore counts in a MutableMap
    private val resourceCounters = mutableMapOf(
        "Coal" to 0,
        "Gold ore" to 0,
        "Mithril ore" to 0,
        "Adamantite ore" to 0,
        "Runite ore" to 0,
        "Golden nugget" to 0,
        "Pay-dirt" to 0
    )

    private val resourceIdMap = mapOf(
        "Coal" to 453,
        "Gold ore" to 444,
        "Mithril ore" to 447,
        "Adamantite ore" to 449,
        "Runite ore" to 451,
    )

    // Increment the count for a specific ore
    fun increment(resourceName: String, increment: Int = 1) {
        if (resourceCounters.containsKey(resourceName)) {
            resourceCounters[resourceName] = resourceCounters[resourceName]!! + increment
            println("Incremented $resourceName by $increment. New count: ${resourceCounters[resourceName]}")
        } else {
            println("Resource $resourceName unknown, can't register gains.")
        }
    }

    // Retrieve the count for a specific ore
    fun getResourceCount(resourceName: String): Int {
        return resourceCounters[resourceName] ?: 0
    }

    // Retrieve all resources and their counts
    fun getAllResourceCounts(): Map<String, Int> {
        return resourceCounters.toMap()
    }

    private fun getResourcePrice(name: String, amount: Int = 0): Int {
        val itemId = resourceIdMap[name]!!
        val collected = this.getResourceCount(name)

        return Pricing.lookupPrice(itemId).orElse(0).times(collected)
    }

    fun getPaintLabelFor(name: String): String {
        return this.getResourceCount(name)
            .toString()
            .plus(" +(")
            .plus(
                Notator.format(
                    this.getResourcePrice(name)
                )
            )
            .plus(")")
    }

    // Helper method to reset all counts back to 0
    fun reset() {
        resourceCounters.keys.forEach { resourceCounters[it] = 0 }
    }
}

