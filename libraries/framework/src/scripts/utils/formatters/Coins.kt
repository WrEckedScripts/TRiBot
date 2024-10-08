package scripts.utils.formatters

import java.util.*

class Coins {
    fun format(coins: Int): String {
        return when {
            coins < 1000 -> coins.toString()
            coins in 1000..99_9999 -> String.format(Locale.US, "%.2fk", coins / 1000.0)
            else -> String.format(Locale.US, "%.2fM", coins / 1000000.0)
        }
    }
}