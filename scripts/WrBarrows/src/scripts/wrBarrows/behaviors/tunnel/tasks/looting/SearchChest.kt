package scripts.wrBarrows.behaviors.tunnel.tasks.looting

import org.tribot.script.sdk.Waiting
import scripts.wrBarrows.managers.Container

class SearchChest(val managers: Container) : AbstractChest() {
    fun satisfied(): Boolean {
        return this.state() == "Closed"
    }

    fun execute(): Boolean {
        this.get()?.interact("Search")

        return Waiting.waitUntil(15_000) {
            this.satisfied()
        }
    }
}