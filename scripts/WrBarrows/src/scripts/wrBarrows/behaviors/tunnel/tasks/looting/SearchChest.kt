package scripts.wrBarrows.behaviors.tunnel.tasks.looting

import org.tribot.script.sdk.Waiting
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container

class SearchChest(val managers: Container) : AbstractChest() {
    fun satisfied(): Boolean {
        return this.state() == "Closed"
    }

    fun execute(): Boolean {
        this.get()?.interact("Search")

        // Allow 8 checkups before we fail the action.
        return Waiting.waitUntil(6_500, FatigueResolver.getMilliseconds()) {
            this.satisfied()
        }
    }
}