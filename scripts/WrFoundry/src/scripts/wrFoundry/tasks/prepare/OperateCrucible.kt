package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.*
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.antiban.FatigueResolver
import scripts.utils.antiban.Lottery
import scripts.utils.antiban.MiniBreak

class OperateCrucible {

    fun execute() {
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

            Waiting.waitUntil(15_000) {
                Waiting.wait(FatigueResolver.getMilliseconds())

                Query.gameObjects()
                    .nameEquals("Mould jig (Poured metal)")
                    .findFirst()
                    .isPresent()
            }

            Waiting.wait(FatigueResolver.getMilliseconds() / 2)

            Query.gameObjects()
                .nameEquals("Mould jig (Poured metal)")
                .findFirst()
                .get()
                .interact("Pick-up")

            Waiting.wait(FatigueResolver.getMilliseconds() * 2)

            Query.equipment()
                .nameContains("Preform")
                .findFirst()
                .isPresent
        }
    }
}