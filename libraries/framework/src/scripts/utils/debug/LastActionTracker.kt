package scripts.utils.debug

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
}