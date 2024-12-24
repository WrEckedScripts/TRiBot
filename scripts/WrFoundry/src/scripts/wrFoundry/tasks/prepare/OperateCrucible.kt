package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.antiban.FatigueResolver
import scripts.utils.antiban.Lottery
import scripts.utils.antiban.MiniBreak
import scripts.wrFoundry.overlay.ResourceCounter
import scripts.wrFoundry.tasks.ExecutableTask
import kotlin.jvm.optionals.getOrNull

class OperateCrucible : ExecutableTask {
    override fun shouldExecute(): Boolean {
        return true
    }

    override fun execute(): Boolean {
        if (!this.shouldExecute()) {
            return false
        }

        Camera.setRotation(TribotRandom.normal(250, 6))
        Camera.setAngle(TribotRandom.normal(90, 3))

        // Grab 14 mithril and 14 addy from bank
        if (!this.ready()) {
            this.builder().execute()
        }

        Waiting.wait(FatigueResolver.getMilliseconds())

        this.fillCrucible()
        this.fill("Mithril bar")

        this.fillCrucible()
        this.fill("Adamantite bar")

        this.pour()

        this.pickup()

        return true
    }

    private fun builder(): BankTask {
        val task = BankTask.builder()
            .addInvItem(2359, Amount.of(14)) // Mithril bars
            .addInvItem(2361, Amount.of(14)) // Adamantite bars
            .build()

        return task
    }

    private fun ready(): Boolean {
        return this.builder().isSatisfied()
    }

    private fun fillCrucible() {
        Waiting.waitUntil(60_000) {
            Query.gameObjects()
                .nameContains("Crucible")
                .findFirst()
                .get()
                .interact("Fill")

            Waiting.wait(FatigueResolver.getMilliseconds())

            MakeScreen.isOpen()
        }
    }

    private fun fill(name: String) {
        Waiting.waitUntil(60_000) {
            MakeScreen.makeAll(name)

            Waiting.waitUntil {
                !MakeScreen.isOpen()
            }

            Waiting.waitUntil {
                ChatScreen.isClickContinueOpen()
            }

            Waiting.wait(FatigueResolver.getMilliseconds())

            Lottery.execute(0.12) {
                MiniBreak.leave()
            }

            val isFilled = Waiting.waitUntil(7_000) {
                Inventory.getCount(name) == 0
            }

            if (isFilled) {
                ResourceCounter.increment(name, 14)
            }

            if (!isFilled) {
                Waiting.wait(FatigueResolver.getMilliseconds() / 2)
            }

            isFilled
        }
    }

    private fun pour() {
        Waiting.waitUntil(60_000) {
            Lottery.execute(0.04) {
                MiniBreak.leave()
            }

            Waiting.wait(FatigueResolver.getMilliseconds())

            val poured = Query.gameObjects()
                .nameEquals("Crucible (full)")
                .findFirst()
                .get()
                .interact("Pour")

            Waiting.wait(FatigueResolver.getMilliseconds() * 2)

            poured
        }
    }

    private fun pickup() {
        Waiting.waitUntil(60_000) {
            Lottery.execute(0.08) {
                MiniBreak.leave()
            }

            Waiting.waitUntil(30_000) {
                Waiting.wait(FatigueResolver.getMilliseconds() / 2)

                Query.gameObjects()
                    .nameEquals("Mould jig (Poured metal)")
                    .findFirst()
                    .isPresent
            }

            Waiting.waitUntil(60_000) {
                val isWearingPerform = Query.equipment()
                    .nameContains("Preform")
                    .findFirst()
                    .isPresent

                if (!isWearingPerform) {
                    Waiting.wait(FatigueResolver.getMilliseconds() / 2)

                    Query.gameObjects()
                        .nameEquals("Mould jig (Poured metal)")
                        .findFirst()
                        .getOrNull()
                        ?.interact("Pick-up")

                    Waiting.wait(FatigueResolver.getMilliseconds() * 3)
                }

                // Re-query for final response
                Query.equipment()
                    .nameContains("Preform")
                    .findFirst()
                    .isPresent
            }
        }

        ResourceCounter.increment("Preforms")
    }
}