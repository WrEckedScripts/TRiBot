package scripts.utils.calculators

import org.tribot.script.sdk.pricing.Pricing
import scripts.utils.formatters.CompactNotator

object ResourceCounter {
    /**
     * Contains a map of resources with their corresponding count
     * For example:
     * - Resources used
     * - Trips completed
     * - Non trade-able items received
     * Or anything else we do not allow id based price lookups on for example.
     */
    private val resourceCounters: MutableMap<String, Int?> = mutableMapOf()

    /**
     * Contains a map of resources which have an in-game ItemId
     */
    private val resourceIdMap: MutableMap<String, Int?> = mutableMapOf()

    fun init(resources: Map<String, Int?>) {
        resources.forEach {
            resourceCounters[it.key] = 0

            it.value?.let { value -> resourceIdMap[it.key] = value }
        }
    }

    fun increment(resourceName: String, increment: Int = 1) {
        if (resourceCounters.containsKey(resourceName)) {
            resourceCounters[resourceName] = resourceCounters[resourceName]!! + increment
            println("Incremented $resourceName by $increment. New count: ${resourceCounters[resourceName]}")
        } else {
            println("Resource $resourceName unknown, can't register gains.")
        }
    }

    fun set(resourceName: String, newCount: Int = 0) {
        if (resourceCounters.containsKey(resourceName)) {
            resourceCounters[resourceName] = newCount
            println("Set $resourceName to $newCount. registered count: ${resourceCounters[resourceName]}")
        } else {
            println("Resource $resourceName unknown, can't register gains.")
        }
    }

    fun getResourceCount(resourceName: String): Int {
        return resourceCounters[resourceName] ?: 0
    }

    private fun getResourcePrice(name: String): Int {
        val itemId = resourceIdMap[name]!!
        val collected = getResourceCount(name)

        return Pricing.lookupPrice(itemId).orElse(0).times(collected)
    }

    fun getPaintLabelFor(name: String): String {
        return getResourceCount(name)
            .toString()
            .plus(" +(")
            .plus(
                CompactNotator.format(
                    getResourcePrice(name)
                )
            )
            .plus(")")
    }

    fun reset() {
        resourceCounters.keys.forEach { resourceCounters[it] = 0 }
    }
}

