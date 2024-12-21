package scripts.wrFoundry.states

import org.tribot.script.sdk.GameState
import org.tribot.script.sdk.query.Query
import kotlin.jvm.optionals.getOrNull

class Commission {
    private val progressVarbitId = 13949

    fun getVarbitValue(): Int {
        return GameState.getVarbit(progressVarbitId)
    }

    // if the varbit is 0, we either don't have a commision
    // or we need to start progressing

    // if at 0 and not wielding sword = no task
    // if at 0 and wielding sword = yes task
    fun isDone(): Boolean {
        return this.getVarbitValue() == 1000 && this.isWieldingPreform()
    }

    fun isStarting(): Boolean {
        return this.getVarbitValue() == 0 && this.isWieldingPreform()
    }

    fun needsTask(): Boolean {
        return this.isDone() == true || this.isWieldingPreform() == false
    }

    fun isWieldingPreform(): Boolean {
        // Check if wielding sword
        val isWieldingPreform = Query.equipment()
            .nameContains("Preform")
            .findFirst()
            .getOrNull()

        return null != isWieldingPreform
    }
}