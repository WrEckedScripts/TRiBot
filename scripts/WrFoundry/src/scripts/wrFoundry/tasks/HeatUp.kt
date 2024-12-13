package scripts.wrFoundry.tasks

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.enums.TemperatureDirection
import scripts.wrFoundry.states.ActiveState
import scripts.wrFoundry.states.HeatVarbit

//TODO
class HeatUp(val heat: Heat) {

    private fun getTargetHeat(): Int {
        return when (ActiveState.current?.temperatureDirection) {
            TemperatureDirection.HEATING -> this.heat.min // Target the start of the bar
            TemperatureDirection.COOLING -> this.heat.max - 50 // Target near the end of the bar
            else -> 1000 // Fully heat-up as a fallback?
        }
    }

    fun shouldExecute(): Boolean {
        return HeatVarbit.getRawValue() < this.getTargetHeat()
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

        Waiting.waitUntil(20_000) {
            // keep checking every second, if we've heated enough.
            HeatVarbit.getRawValue() > this.getTargetHeat()
        }

        // Step out of operating the object
        return MyPlayer.getTile().click() && interacted
    }
}