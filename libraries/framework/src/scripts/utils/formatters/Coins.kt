package scripts.utils.formatters

import java.util.*

class Coins {
    fun format(coins: Int): String {
        return when {
            coins < 1000 -> coins.toString()
            coins in 1000..999999 -> String.format(Locale.US, "%.2fk", coins / 1000.0)
            coins in 999999..9999999 -> String.format(Locale.US, "%.2fM", coins / 1000000.0)
            else -> coins.toString() // For values 10 million and above, you can adjust as needed
        }
    }
}