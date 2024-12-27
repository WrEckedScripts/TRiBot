package scripts.wrFoundry.overlay

import org.tribot.script.sdk.pricing.Pricing
import scripts.utils.formatters.CompactNotator

object ResourceCounter {
    // Initialize ore counts in a MutableMap
    private val resourceCounters = mutableMapOf(
        "Mithril bar" to 0,
        "Adamantite bar" to 0,
        "Preforms" to 0
    )

    private val resourceIdMap = mapOf(
        "Mithril bar" to 2359,
        "Adamantite bar" to 2361,
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

    private fun getResourcePrice(name: String): Int {
        val itemId = resourceIdMap[name]!!
        val collected = this.getResourceCount(name)

        return Pricing.lookupPrice(itemId).orElse(0).times(collected)
    }

    fun getPaintLabelFor(name: String): String {
        return this.getResourceCount(name)
            .toString()
            .plus(" +(")
            .plus(
                CompactNotator.format(
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

