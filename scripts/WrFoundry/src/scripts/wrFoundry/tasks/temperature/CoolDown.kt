package scripts.wrFoundry.tasks.temperature

import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.enums.TemperatureDirection
import scripts.wrFoundry.states.CurrentProcessingTask

class CoolDown(heat: Heat) : TemperatureAdjustmentTask(heat) {

    override val interactableName: String = "Waterfall"
    override val action: String = "Cool-preform"

    override fun getTargetHeat(): Int {
        return when (CurrentProcessingTask.current?.temperatureDirection) {
            TemperatureDirection.HEATING -> this.heat.min + 30 // Target the start of the bar
            TemperatureDirection.COOLING -> this.heat.max - 50 // Target near the end of the bar
            else -> 0
        }
    }

    override fun shouldExecuteCondition(currentHeat: Int, targetHeat: Int): Boolean {
        return currentHeat > targetHeat
    }

    override fun hasAchievedDesiredHeat(currentHeat: Int, targetHeat: Int): Boolean {
        return currentHeat <= targetHeat
    }
}