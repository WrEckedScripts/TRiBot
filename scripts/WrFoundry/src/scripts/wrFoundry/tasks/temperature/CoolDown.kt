package scripts.wrFoundry.tasks.temperature

import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.enums.TemperatureDirection
import scripts.wrFoundry.states.ActiveState
import scripts.wrFoundry.states.HeatVarbit

class CoolDown(heat: Heat) {
    val currentHeat = HeatVarbit.getRawValue()

    private val startOfTemperatureGauge = heat.min + 15 // @todo to randomize this
    private val endOfTemperatureGauge = heat.max - 15 // @todo to randomize this

    fun getTargetHeat(): Int {
        return when (ActiveState.current?.temperatureDirection) {
            TemperatureDirection.HEATING -> this.startOfTemperatureGauge
            TemperatureDirection.COOLING -> this.endOfTemperatureGauge
            else -> 1000 // Fully heat-up as a fallback?
        }
    }
}