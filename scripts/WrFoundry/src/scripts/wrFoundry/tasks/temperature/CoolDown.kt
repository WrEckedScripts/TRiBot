package scripts.wrFoundry.tasks.temperature

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.enums.TemperatureDirection
import scripts.wrFoundry.states.ActiveState
import scripts.wrFoundry.states.HeatVarbit

//TODO
class CoolDown(val heat: Heat) {

    private fun getTargetHeat(): Int {
        return when (ActiveState.current?.temperatureDirection) {
            TemperatureDirection.HEATING -> this.heat.max - 50 // Target the end of the bar
            TemperatureDirection.COOLING -> this.heat.min // Target near the start of the bar
            else -> 0
        }
    }

    fun shouldExecute(): Boolean {
        Logger("[CoolDown@shouldExecute::raw]").warn("${HeatVarbit.getRawValue()}")
        Logger("[CoolDown@shouldExecute::target]").warn("${this.getTargetHeat()}")
        return HeatVarbit.getRawValue() > this.getTargetHeat() && HeatVarbit.get() != ActiveState.currentHeat
    }

    fun execute(): Boolean {
        //TODO operate the heating pool until currentHeatValue is as/near the target
        val interactableName = "Waterfall"
        val action = "Cool-preform"

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
            // Cancel cooling if the varbit is below our target
            if (HeatVarbit.getRawValue() <= this.getTargetHeat()) {
                MyPlayer.getTile().click()
                return@waitUntil true
            }

            return@waitUntil false
        }

        // Step out of operating the object
        return finished
    }
}