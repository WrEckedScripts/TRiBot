package scripts.utils.formatters

import java.util.*

object CompactNotator {
    fun format(value: Int): String {
        return when {
            value < 1000 -> value.toString()
            value in 1000..99_9999 -> String.format(Locale.US, "%.2fk", value / 1000.0)
            else -> String.format(Locale.US, "%.2fM", value / 1000000.0)
        }
    }
}