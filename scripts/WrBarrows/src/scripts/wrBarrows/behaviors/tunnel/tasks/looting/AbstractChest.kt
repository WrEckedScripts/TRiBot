package scripts.wrBarrows.behaviors.tunnel.tasks.looting

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import kotlin.jvm.optionals.getOrNull

abstract class AbstractChest {
    fun get(): GameObject? {
        return Query.gameObjects()
            .nameEquals("Chest")
            .findBestInteractable()
            .getOrNull()
    }

    //TODO enum for values
    fun state(): String {
        if (this.get()?.actions?.contains("Open") == true) {
            return "Closed"
        }

        return "Open"
    }
}