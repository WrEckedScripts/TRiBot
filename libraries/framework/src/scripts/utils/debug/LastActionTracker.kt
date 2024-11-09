package scripts.utils.debug

object LastActionTracker {

    private val trackers: MutableMap<String, Long> = mutableMapOf()

    fun track(tracker: String) {
        trackers[tracker] = System.currentTimeMillis()
    }

    fun show(tracker: String): Long {
        return trackers[tracker] ?: System.currentTimeMillis()
    }
}