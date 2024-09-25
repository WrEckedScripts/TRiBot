package scripts.utils.formatters

class Countdown {
    fun fromMillis(milliseconds: Long): String {
        // Calculate total seconds
        val nextPayTime = (System.currentTimeMillis() - milliseconds)
        val totalSeconds = (nextPayTime / 1000).toInt()

        // Calculate minutes and seconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        // Format minutes and seconds as "MM:SS"
        return String.format("%02d:%02d", minutes, seconds)
    }
}