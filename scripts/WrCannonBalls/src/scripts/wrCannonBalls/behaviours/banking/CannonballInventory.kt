package scripts.wrCannonBalls.behaviours.banking

import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask
import scripts.wrCannonBalls.managers.Settings

class CannonballInventory {
    fun builder(): BankTask {
        val task = BankTask.builder()
            .addInvItem(Settings.mouldId, Amount.of(1)) // (Double) Ammo mould
            .addInvItem(2, Amount.fill(0)) // Cannonball
            .addInvItem(2353, Amount.range(1, 26)) // Steel bars
            .build()

        return task
    }

    fun ready(): Boolean {
        return this.builder().isSatisfied()
    }

}