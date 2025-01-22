package scripts.wrMotherlode.banking.actions

import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import scripts.utils.antiban.FatigueResolver

class MineReadyInventoryBuilder {
    fun clearPayDirt() {
        Query.inventory()
            .nameEquals("Pay-dirt")
            .forEach {
                it.click("Drop")
            }

        FatigueResolver.await()
    }

    fun task(): BankTask {
        val bankTask = BankTask.builder()
            .addInvItem(1271, Amount.of(1)) // Adamant Pickaxe
//            .addInvItem(1275, Amount.of(1)) // Rune Pickaxe
//          .addInvItem(11920, Amount.of(1)) // Dragon Pickaxe
            .addInvItem(2347, Amount.of(1)) // Hammer
            // Pay-dirt, although not bankable, we should accept them, this avoids trying to bank them.
            .addInvItem(12011, Amount.range(0, 26))
            .build()

        return bankTask
    }
}