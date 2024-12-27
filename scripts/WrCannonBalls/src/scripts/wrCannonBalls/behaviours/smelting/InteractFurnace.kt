package scripts.wrCannonBalls.behaviours.smelting

import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.antiban.Lottery
import scripts.utils.antiban.MiniBreak
import scripts.utils.failsafes.LastActionTracker
import scripts.wrCannonBalls.managers.Container
import kotlin.random.Random

class InteractFurnace(
    val logger: Logger,
    val managers: Container
) {
    fun execute(): Boolean {
        LastActionTracker.track("state")

        if (MakeScreen.isOpen()) {
            MakeScreen.makeAll("Cannonball")

            return true
        }

        val furnace = Query.gameObjects()
            .nameEquals("Furnace")
            .findBestInteractable()

        if (furnace.isPresent) {
            furnace.get().interact("Smelt")
            LastActionTracker.track("click")

            val result = Waiting.waitUntil {
                MakeScreen.isOpen()

                Lottery.execute(Random.nextDouble(0.05, 0.09)) {
                    MiniBreak.fatigueLeave()
                }

                Waiting.wait(FatigueResolver.getMilliseconds())

                MakeScreen.makeAll("Cannonball")
            }

            MiniBreak.leave()

            return result
        }

        return false
    }
}