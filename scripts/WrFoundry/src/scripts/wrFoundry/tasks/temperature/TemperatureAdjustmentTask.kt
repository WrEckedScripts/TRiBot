package scripts.wrFoundry.tasks.temperature

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.wrFoundry.enums.Heat
import scripts.wrFoundry.states.CurrentProcessingTask
import scripts.wrFoundry.states.HeatLevel
import scripts.wrFoundry.tasks.ExecutableTask

abstract class TemperatureAdjustmentTask(protected val heat: Heat) : ExecutableTask {
    protected abstract val interactableName: String
    protected abstract val action: String

    protected abstract fun shouldExecuteCondition(currentHeat: Int, targetHeat: Int): Boolean
    protected abstract fun hasAchievedDesiredHeat(currentHeat: Int, targetHeat: Int): Boolean
    protected abstract fun getTargetHeat(): Int

    override fun shouldExecute(): Boolean {
        val currentHeat = HeatLevel.getRawValue()
        val targetHeat = getTargetHeat()

        Logger("[${this.javaClass.simpleName}@shouldExecute::raw]").warn("$currentHeat")
        Logger("[${this.javaClass.simpleName}@shouldExecute::target]").warn("$targetHeat")

        return shouldExecuteCondition(currentHeat, targetHeat) && HeatLevel.get() != CurrentProcessingTask.currentHeat
    }

    override fun execute(): Boolean {
        val interacted = Query.gameObjects()
            .nameEquals(interactableName)
            .findFirst()
            .map { interactable -> interactable.interact(action) }
            .orElse(false)

        if (!interacted) {
            return false
        }

        val finished = Waiting.waitUntil(20_000, 100) {
            val currentHeat = HeatLevel.getRawValue()
            val targetHeat = getTargetHeat()

            // Check if the task condition has been met
            if (hasAchievedDesiredHeat(currentHeat, targetHeat)) {
                MyPlayer.getTile().click() // Cancel interaction once the condition is met
                return@waitUntil true
            }
            false
        }

        return finished
    }
}