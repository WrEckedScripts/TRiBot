package scripts.utils.formatters

object TimeNotator {
    fun formatMillisecondsToHMS(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%d:%02d:%02d", hours, minutes, seconds)
    }
}