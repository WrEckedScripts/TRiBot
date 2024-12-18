package scripts.wrFoundry.tasks.processing

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.wrFoundry.enums.InteractableMachine
import scripts.wrFoundry.enums.Stage
import scripts.wrFoundry.states.CurrentProcessingTask
import scripts.wrFoundry.states.HeatLevel

class ProcessingTask(private val interactableMachine: InteractableMachine, private val stage: Stage) {
    private fun shouldExecute(): Boolean {
        return HeatLevel.get() == stage.heat && CurrentProcessingTask.get() == stage
    }

    fun execute(): Boolean {
        if (!this.shouldExecute()) {
            return false
        }

        val interactableName = this.interactableMachine.objectName
        val action = "Use"

        val interacted = Query.gameObjects()
            .nameEquals(interactableName)
            .findFirst()
            .map { interactable -> interactable.interact(action) }
            .orElse(false)

        val completed = Waiting.waitUntil(25_000) {
            !this.shouldExecute()
        }

        // Stop further processing.
        MyPlayer.getTile().click()

        return interacted && completed
    }
}