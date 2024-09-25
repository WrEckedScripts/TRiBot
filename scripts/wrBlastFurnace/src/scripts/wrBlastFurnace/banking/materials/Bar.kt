package scripts.wrBlastFurnace.banking.materials

import org.tribot.script.sdk.pricing.Pricing
import java.util.*

class Bar(val name: String, val id: Int) {
    fun name(): String {
        return this.name
    }

    fun price(): Optional<Int> {
        return Pricing.lookupPrice(this.id)
    }

    fun priceTimes(quantity: Int): Int {
        val price: Int = this.price().orElse(0)

        return price.times(quantity)
    }
}