package scripts.utils.antiban

import scripts.utils.Logger
import java.time.LocalDateTime
import kotlin.math.ceil

object RuntimeTracker {
    var startedAt: Long? = null
    var logger: Logger? = null

    fun initLogger(log: Logger) {
        this.logger = log
    }

    fun init() {
        this.startedAt = System.currentTimeMillis()
        this.logger?.debug("[RuntimeTracker] - started now")
    }

    // Used to keep track of how long the script has been running
    fun hours(): Int {
        this.logger?.debug("[RuntimeTracker] - hours: ${(this.calculate() / 3600000.0).toInt()}")
        return (this.calculate() / 3600000.0).toInt()
    }

    fun minutes(): Int {
        this.logger?.debug("[RuntimeTracker] - minutes: ${ceil((this.calculate() % 3600000) / 60000.0).toInt()}")
        return ceil((this.calculate() % 3600000) / 60000.0).toInt()
    }

    // Usable for Fatigue per time of day
    fun currentHour(): Int {
        this.logger?.debug("[RuntimeTracker] - currentHour: ${LocalDateTime.now().hour}")
        return LocalDateTime.now().hour
    }

    private fun calculate(): Long {
        if (this.startedAt == null) {
            this.init()
        }
        this.logger?.debug("[RuntimeTracker] - calculate: ${System.currentTimeMillis() - this.startedAt!!}")
        return System.currentTimeMillis() - this.startedAt!!
    }
}