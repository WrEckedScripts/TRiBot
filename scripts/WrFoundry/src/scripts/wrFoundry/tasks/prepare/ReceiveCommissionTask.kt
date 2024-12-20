package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrFoundry.enums.Commission
import kotlin.jvm.optionals.getOrNull

class ReceiveCommissionTask {

    private fun shouldExecute(): Boolean {
        return true//todo
    }

    fun execute(): Boolean {
        if (!this.shouldExecute()) {
            return false
        }

        // NPC = Kovac
        val kovacNpc = Query.npcs()
            .nameEquals("Kovac")
            .findFirst()
            .get()

        //TODO re-enable
        kovacNpc.interact("Hand-in")
        Waiting.waitUntil { ChatScreen.isClickContinueOpen() }
        ChatScreen.clickContinue()
        Waiting.wait(FatigueResolver.getMilliseconds())

        kovacNpc.interact("Commission")
        Waiting.waitUntil { ChatScreen.isClickContinueOpen() }

        var currentTask = ""
        Waiting.waitUntil(15_000) {
            if (currentTask == "") {
                val widget = Query.widgets()
                    .inIndexPath(231, 6)
                    .findFirst()
                    .getOrNull()

                if (widget == null) {
                    return@waitUntil false
                }

                Logger("[ReceiveCommissionTask]").debug(widget.text)
                currentTask = this.extractCommission(widget.text.toString())
            }

            Waiting.waitUntil(2_000) { currentTask != "" }
            ChatScreen.clickContinue()
            Waiting.wait(FatigueResolver.getMilliseconds() * 2)
            !ChatScreen.isClickContinueOpen()
        }

        Logger("[ReceiveCommissionTask]")
            .info("Got task for: ${currentTask}")

        val commission = Commission.fromString(currentTask)

        if (null == commission) {
            Logger("[CommissionCheck]")
                .info("is null..?")
            return false
        }

        val res = SetupMould(commission).execute()

        Logger("[Setup]").debug("Done setting up... ${res}")

        // time to bank and fill the crucible
        FillCrucible().execute()


        // create object out of the extracted value.

        // Time to set-up the mould

        // If option "Hand-in" exists && Wearing a preform
        // We need to hand-in and collect a new task (option "yes")

        // Then we need to Setup the mould based on the received task
        // - needs to extract from the string
        // - map to the correct combinations
        // - handle the UI

        // Then we need to fill the crucible
        // - 14 adamantite bars
        // - 14 mithril bars

        // If crucible is full, pour and collect the sword from mould

        return true
    }

    fun extractCommission(input: String): String {
        // Regex to capture text inside <col=...> and </col>, allowing <br> between words
        val regex = """<col=[^>]+>(.*?)(?:<br>\s*<col=[^>]+>)?(.*?)</col>""".toRegex()
        val match = regex.find(input)
        Logger("Matcher").debug(match?.groups.toString())

        // Combine both groups to form the full name
        return if (match != null) {
            "${match.groups[1]?.value} ${match.groups[2]?.value}".trim()
        } else {
            ""
        }
    }

}