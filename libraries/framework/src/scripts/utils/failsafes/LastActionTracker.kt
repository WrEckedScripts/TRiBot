package scripts.utils.failsafes

import kotlin.math.ceil

object LastActionTracker {

    private val trackers: MutableMap<String, Long> = mutableMapOf()

    fun track(tracker: String) {
        trackers[tracker] = System.currentTimeMillis()
    }

    fun show(tracker: String): Long {
        return trackers[tracker] ?: System.currentTimeMillis()
    }

    fun getElapsedMinutes(tracker: String): Int {
        val startTime = trackers[tracker] ?: return 0
        val elapsedMilliseconds = System.currentTimeMillis() - startTime

        return ceil(
            elapsedMilliseconds / 60000.0
        ).toInt()
    }

    fun throwExceptionIfNoActionSince(tracker: String, minutesAgo: Int = 10) {
        if (getElapsedMinutes(tracker) >= minutesAgo) {
            throw Exception("No action since $minutesAgo minutes")
        }
    }
}