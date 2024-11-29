package scripts.wrCannonBalls.behaviours.smelting

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.utils.debug.LastActionTracker
import scripts.wrCannonBalls.behaviours.banking.CanSmeltBalls
import scripts.wrCannonBalls.managers.Container


fun isSmeltingBalls(logger: Logger, managers: Container): Boolean {
    val initialBallCount = Query.inventory()
        .nameEquals("Cannonball")
        .sumStacks()

    // Give the script roughly +3 seconds to start animating, otherwise we are not smelting
    if (Waiting.waitUntilAnimating(3_000 + FatigueResolver.getMilliseconds())) {
        // If we're animating, let's wait until we're not anymore.
        // Ball smelting takes
        val result = Waiting.waitUntil(60_000, 10_000 + FatigueResolver.getMilliseconds()) {
            val holdingBallCount = Query.inventory()
                .nameEquals("Cannonball")
                .sumStacks()

            val holdingBarCount = Query.inventory().nameEquals("Steel bar").count()

            !MyPlayer.isAnimating()
                    || initialBallCount == holdingBallCount
                    || holdingBarCount == 0
        }

        return result
    }

    if (LastActionTracker.getElapsedMinutes("state") >= 8) {
        logger.error("Killing script to safeguard your account.")
        throw Exception("State tracker elapsed 8 minutes, whilst we're failing to smith..")
    }

    logger.error("isSmeltingBalls returns 'FALSE'")
    logger.error("In same state for ${LastActionTracker.getElapsedMinutes("state")} minutes")

    // Check if we still hold bars, if so, we can smelt.
    // Then we simply need to re-interact.
    // This is for example when we dismiss a random or whatever, we do failsafe for just standing still.
    if (CanSmeltBalls().ready()) {
        logger.info("Re-initing interact")
        InteractFurnace(logger, managers).execute()
    }

    return false
}