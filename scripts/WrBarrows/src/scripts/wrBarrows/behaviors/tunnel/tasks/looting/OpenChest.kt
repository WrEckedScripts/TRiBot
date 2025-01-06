package scripts.wrBarrows.behaviors.tunnel.tasks.looting

import org.tribot.script.sdk.Waiting
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.managers.Container

class OpenChest(val managers: Container) : AbstractChest() {
    fun satisfied(): Boolean {
        return this.state() == "Open"
    }

    fun execute(): Boolean {
        this.get()?.interact("Open")

        return Waiting.waitUntil(15_000, FatigueResolver.getMilliseconds()) {
            this.satisfied()
        }
    }
}