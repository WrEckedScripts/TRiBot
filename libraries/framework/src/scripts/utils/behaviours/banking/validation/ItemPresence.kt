package scripts.utils.behaviours.banking.validation

import org.tribot.script.sdk.query.Query

/**
 * Validation helper object, to ensure we've got enough stock, in bank, of a certain item,
 * before we execute related behaviour with/on it.
 */
object ItemPresence {
    fun throwExceptionIfBankMissesItem(name: String, quantity: Int? = null) {
        val bankItemCount = Query.bank()
            .nameEquals(name)
            .sumStacks()

        if (quantity != null) {
            if (bankItemCount < quantity) {
                throw Exception("We do not have enough stock of ${name} got ${bankItemCount} need ${quantity}")
            }
        }

        if (bankItemCount <= 0) {
            throw Exception("We are missing stock for ${name}")
        }
    }
}