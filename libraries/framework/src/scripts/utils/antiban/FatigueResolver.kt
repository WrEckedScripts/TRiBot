package scripts.utils.antiban

import org.tribot.api.input.Mouse
import org.tribot.script.sdk.antiban.AntibanProperties
import org.tribot.script.sdk.antiban.PlayerPreferences
import scripts.utils.Logger
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

object FatigueResolver {
    val defaultSpeed = Mouse.getSpeed()

    var minProp: Double = 450.0
    var maxProp: Double = 1250.0
    var sdProp: Double = 90.0
    var logger: Logger? = null

    private fun getMinMaxMouseSpeeds(): Pair<Int, Int> {
        val min = PlayerPreferences.preference(ProfilingPreferences.MIN_MOUSE_SPEED.key) { g ->
            g.uniform(80, 110)
        }

        val max = PlayerPreferences.preference(ProfilingPreferences.MAX_MOUSE_SPEED.key) { g ->
            g.uniform(130, 160)
        }

        return Pair(min, max)
    }

    fun initLogger(log: Logger) {
        this.logger = log
    }

    /*
     * Helper method to customize the Antiban properties for calculating the waiting modifiers
     */
    fun customize(min: Double? = null, max: Double? = null, sd: Double? = null) {
        if (min != null) {
            this.minProp = min
        }

        if (max != null) {
            this.maxProp = max
        }

        if (sd != null) {
            this.sdProp = sd
        }
    }

    private fun adjustMouseSpeed(runtime: Int? = null, currentHour: Int? = null) {
        var runtimeValue = runtime
        var currentHourValue = currentHour

        if (runtime == null) {
            runtimeValue = RuntimeTracker.hours()
        }
        if (currentHour == null) {
            currentHourValue = RuntimeTracker.currentHour()
        }

        val factor = getFactor(runtimeValue!!, currentHourValue!!)

        val coercedInSpeed = (this.defaultSpeed / factor).toInt()
            .coerceIn(
                this.getMinMaxMouseSpeeds().first,
                this.getMinMaxMouseSpeeds().second
            )
        Mouse.setSpeed(coercedInSpeed)
    }

    private fun calculateDelay(mean: Double, sd: Double = (this.maxProp - this.minProp) / 10.0): Pair<Int, Int> {
        val u1 = 1.0 - Random.nextDouble()
        val u2 = 1.0 - Random.nextDouble()

        val rndStdNormal = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
        val rndNormal = mean + sd * rndStdNormal

        return Pair(
            rndNormal.toInt(),
            rndStdNormal.toInt()
        )
    }

    /**
     * Influences the provided delays by adjusting the mean and standard deviation,
     * based on both the current time of day and how long the bot has been running
     */
    fun getMilliseconds(
        runtime: Int? = null,
        currentHour: Int? = null,
        sd: Double = (this.maxProp - this.minProp) / 10.0,
    ): Int {
        var runtimeValue = runtime
        var currentHourValue = currentHour

        if (runtime == null) {
            runtimeValue = RuntimeTracker.hours()
        }
        if (currentHour == null) {
            currentHourValue = RuntimeTracker.currentHour()
        }

        val factor = getFactor(runtimeValue!!, currentHourValue!!)
        val adjustedMean = ((this.minProp + this.maxProp) / 2) * factor
        val adjustedSd = sd * factor

        // Override any props, for unhandled waits by us, to still ensure our fatique system is in effect.
        AntibanProperties.Props().waitingMaxModifier = this.maxProp
        AntibanProperties.Props().waitingMinModifier = this.minProp
        AntibanProperties.Props().waitingNormalDistStdModifier = adjustedSd

        this.logger?.warn(
            "[Fatique] - factor: ${factor} | mean: ${adjustedMean} | sd: ${adjustedSd} | calculatedDelay: ${
                calculateDelay(
                    adjustedMean,
                    adjustedSd
                ).first
            }}"
        )

        Lottery.execute(probability = Random.nextDouble(0.62, 0.84)) {
            this.adjustMouseSpeed(runtimeValue, currentHourValue)
        }

        return calculateDelay(adjustedMean, adjustedSd).first
    }

    /**
     * Calculates the fatique factor in play, based off the time of day and the time running the bot.
     */
    private fun getFactor(
        runtime: Int,
        currentHour: Int
    ): Double {
        val timeOfDayModifier = when {
            currentHour in 4..10 -> Random.nextDouble(0.6, 0.85) // Early morning - focused
            currentHour in 11..17 -> Random.nextDouble(0.95, 1.05) // Daytime - standard
            currentHour in 18..21 -> Random.nextDouble(1.15, 1.3) // Early evening - getting tired
            else -> Random.nextDouble(1.3, 1.45) // Night - tired
        }

        val runtimeFatique = when {
            runtime < 2.0 -> Random.nextDouble(0.85, 1.05) // First two hours - fully alert
            runtime < 6.0 -> Random.nextDouble(1.05, 1.25) // 2 tot 6 hours - normal
            runtime < 10.0 -> Random.nextDouble(1.25, 1.35) // 6 to 10 hours - getting tired
            runtime < 16 -> Random.nextDouble(1.35, 1.5) // 10 to 16 hours - a bit more tired
            else -> Random.nextDouble(1.5, 1.6) // After 10 hours - tired
        }

        return timeOfDayModifier * runtimeFatique
    }
}