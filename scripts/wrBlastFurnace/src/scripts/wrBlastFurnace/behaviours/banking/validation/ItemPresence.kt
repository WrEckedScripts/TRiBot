package scripts.wrBlastFurnace.behaviours.banking.validation

import org.tribot.script.sdk.query.Query

object ItemPresence {
    //todo untested as of 1.3.0
    fun throwExceptionIfBankMissesItem(name: String?) {
        val bankHasItem = Query.bank()
            .nameEquals(name)
            .count() > 0

        if (!bankHasItem) {
            throw Exception("We have ran out of ${name}")
        }
    }
}