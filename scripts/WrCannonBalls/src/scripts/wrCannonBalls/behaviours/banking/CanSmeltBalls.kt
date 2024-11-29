package scripts.wrCannonBalls.behaviours.banking

import org.tribot.script.sdk.tasks.Amount
import org.tribot.script.sdk.tasks.BankTask

class CanSmeltBalls {
    fun builder(): BankTask {
        val task = BankTask.builder()
            .addInvItem(4, Amount.of(1)) // Ammo mould
            .addInvItem(2, Amount.fill(0)) // Cannonball
            .addInvItem(2353, Amount.range(1, 26)) // Steel bars
            .build()

        return task
    }

    fun ready(): Boolean {
        return this.builder().isSatisfied()
    }

}