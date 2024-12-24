package scripts.wrFoundry.tasks.temperature

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.enums.TemperatureDirection
import scripts.wrFoundry.states.CurrentProcessingTask
import scripts.wrFoundry.states.HeatLevel
import scripts.wrFoundry.tasks.ExecutableTask

/**
 * @deprecated
 *
 * Kept in VCS to ensure we can quickly spot missing stuff, when we go and validate the newer setup
 */
class OldCoolDown(val heat: Heat) : ExecutableTask {

    private fun getTargetHeat(): Int {
        return when (CurrentProcessingTask.current?.temperatureDirection) {
            TemperatureDirection.HEATING -> this.heat.min + 30 // Target the start of the bar
            TemperatureDirection.COOLING -> this.heat.max // Target near the end of the bar
            else -> 0
        }
    }

    override fun shouldExecute(): Boolean {
        Logger("[CoolDown@shouldExecute::raw]").warn("${HeatLevel.getRawValue()}")
        Logger("[CoolDown@shouldExecute::target]").warn("${this.getTargetHeat()}")
        return HeatLevel.getRawValue() > this.getTargetHeat() && HeatLevel.get() != CurrentProcessingTask.currentHeat
    }

    override fun execute(): Boolean {
        //TODO operate the heating pool until currentHeatValue is as/near the target
        val interactableName = "Waterfall"
        val action = "Cool-preform"

        val interacted = Query.gameObjects()
            .nameEquals(interactableName)
            .findFirst()
            .map { interactable -> interactable.interact(action) }
            .orElse(false)

        if (!interacted) {
            return false
        }

        val finished = Waiting.waitUntil(20_000, 200) {
            // Cancel cooling if the varbit is below our target
            if (HeatLevel.getRawValue() <= this.getTargetHeat()) {
                // TODO, this is prone for profiling, figure out better approach
                MyPlayer.getTile().click()
                return@waitUntil true
            }

            return@waitUntil false
        }

        // Step out of operating the object
        return finished
    }
}