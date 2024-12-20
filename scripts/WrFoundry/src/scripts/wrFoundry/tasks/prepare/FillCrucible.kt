package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.MakeScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import scripts.utils.antiban.FatigueResolver

class FillCrucible {

    fun execute() {
        // Grab 14 mithril and 14 addy from bank
        if (this.ready() == false) {
            this.builder().execute()
        }

        this.fillCrucible()
        this.fill("Mithril bar")


        this.fillCrucible()
        this.fill("Adamantite bar")

        this.pour()

        this.pickup()
    }

    fun builder(): BankTask {
        val task = BankTask.builder()
            .addInvItem(2359, Amount.of(14)) // Mithril bars
            .addInvItem(2361, Amount.of(14)) // Adamantite bars
            .build()

        return task
    }

    fun ready(): Boolean {
        return this.builder().isSatisfied()
    }

    fun fillCrucible() {
        Query.gameObjects()
            .nameContains("Crucible")
            .findFirst()
            .get()
            .interact("Fill")

        Waiting.waitUntil {
            MakeScreen.isOpen()
        }
    }

    fun fill(name: String) {
        MakeScreen.makeAll(name)

        Waiting.waitUntil {
            !MakeScreen.isOpen()
        }

        Waiting.waitUntil {
            ChatScreen.isClickContinueOpen()
        }
    }

    fun pour() {
        Waiting.wait(FatigueResolver.getMilliseconds())

        Query.gameObjects()
            .nameEquals("Crucible (full)")
            .findFirst()
            .get()
            .interact("Pour")

        Waiting.wait(FatigueResolver.getMilliseconds() * 2)
    }

    fun pickup() {
        Waiting.waitUntil {
            Waiting.wait(FatigueResolver.getMilliseconds())

            Query.gameObjects()
                .nameEquals("Mould jig (Poured metal)")
                .findFirst()
                .isPresent()
        }

        Query.gameObjects()
            .nameEquals("Mould jig (Poured metal)")
            .findFirst()
            .get()
            .interact("Pick-up")

        Waiting.waitUntil {
            Waiting.wait(FatigueResolver.getMilliseconds())

            Query.equipment()
                .nameContains("Preform")
                .findFirst()
                .isPresent
        }
    }
}