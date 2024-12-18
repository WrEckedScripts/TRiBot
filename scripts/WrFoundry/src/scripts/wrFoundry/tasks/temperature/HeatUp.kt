package scripts.wrFoundry.tasks.temperature

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.enums.TemperatureDirection
import scripts.wrFoundry.states.CurrentProcessingTask
import scripts.wrFoundry.states.HeatLevel

//TODO
class HeatUp(val heat: Heat) {

    private fun getTargetHeat(): Int {
        return when (CurrentProcessingTask.current?.temperatureDirection) {
            TemperatureDirection.HEATING -> this.heat.min + 30 // Target the start of the bar
            TemperatureDirection.COOLING -> this.heat.max - 50 // Target near the end of the bar
            else -> 0
        }
    }

    fun shouldExecute(): Boolean {
        Logger("[HeatUp@shouldExecute::raw]").warn("${HeatLevel.getRawValue()}")
        Logger("[HeatUp@shouldExecute::target]").warn("${this.getTargetHeat()}")
        return HeatLevel.getRawValue() < this.getTargetHeat() && HeatLevel.get() != CurrentProcessingTask.currentHeat
    }

    fun execute(): Boolean {
        //TODO operate the heating pool until currentHeatValue is as/near the target
        val interactableName = "Lava pool"
        val action = "Heat-preform"

        val interacted = Query.gameObjects()
            .nameEquals(interactableName)
            .findFirst()
            .map { interactable -> interactable.interact(action) }
            .orElse(false)

        if (!interacted) {
            //TODO failed to interact..?
            return false
        }

        val finished = Waiting.waitUntil(20_000, 200) {
            // Cancel heating if the varbit is higher than target
            if (HeatLevel.getRawValue() >= this.getTargetHeat()) {
                MyPlayer.getTile().click()
                return@waitUntil true
            }

            return@waitUntil false
        }

        // Step out of operating the object
        return finished
    }
}