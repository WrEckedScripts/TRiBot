package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Npc
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrFoundry.enums.Commission
import scripts.wrFoundry.managers.Container
import kotlin.jvm.optionals.getOrNull

class ReceiveCommissionTask(val managers: Container) {
    private var extractedCommission: String = ""


    private fun shouldExecute(): Boolean {
        return true//todo
    }

    private fun getKovacNpc(): Npc {
        return Query.npcs()
            .nameEquals("Kovac")
            .findFirst()
            .get()
    }

    fun execute(): Boolean {
        if (!this.shouldExecute()) {
            return false
        }

        this.handIn()

        this.extractedCommission = this.getCommissionTask()

        if (this.extractedCommission == "") {
            managers.repetitiveActionManager.increment("receive-commission", 5)
            this.execute()

            return false
        }

        val commission = Commission.fromString(
            this.extractedCommission
        )

        if (null == commission) {
            managers.repetitiveActionManager.increment("resolve-commission", 3)
            this.execute()

            return false
        }

        managers.repetitiveActionManager.reset("receive-commission")
        managers.repetitiveActionManager.reset("resolve-commission")

        Logger("[ReceiveCommissionTask]").warn("START - SetupMould.exec")
        SetupMould(commission).execute()
        Logger("[ReceiveCommissionTask]").warn("END - SetupMould.exec")

        // time to bank and fill the crucible
        OperateCrucible().execute()

        return true
    }

    private fun getCommissionTask(): String {
        var commission = ""

        this.getKovacNpc().interact("Commission")

        Waiting.waitUntil { ChatScreen.isClickContinueOpen() }

        Waiting.waitUntil(15_000) {
            if (commission == "") {
                val widget = Query.widgets()
                    .inIndexPath(231, 6)
                    .findFirst()
                    .getOrNull()

                if (widget == null) {
                    return@waitUntil false
                }

                Logger("[ReceiveCommissionTask]").debug(widget.text)
                commission = this.extractCommissionFrom(widget.text.toString())
            }

            Waiting.waitUntil(2_000) { commission != "" }
            ChatScreen.clickContinue()
            Waiting.wait(FatigueResolver.getMilliseconds() * 2)
            !ChatScreen.isClickContinueOpen()
        }

        return commission
    }

    private fun handIn() {
        this.getKovacNpc().interact("Hand-in")

        Waiting.waitUntil { ChatScreen.isClickContinueOpen() }

        ChatScreen.clickContinue()

        Waiting.wait(FatigueResolver.getMilliseconds())
    }

    private fun extractCommissionFrom(input: String): String {
        val regex = """<col=[^>]+>([^<]*)(?:<br>\s*<col=[^>]+>)?([^<]*)</col>""".toRegex()
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