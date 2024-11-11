package scripts.wrBlastFurnace.banking.materials

import org.tribot.script.sdk.pricing.Pricing
import java.util.*

class Ore(val name: String, val quantity: Int, val id: Int) {
    fun name(): String {
        return this.name
    }

    fun quantity(): Int {
        return this.quantity
    }

    fun id(): Int {
        return this.id
    }

    fun price(): Optional<Int> {
        return Pricing.lookupPrice(this.id)
    }

    fun priceTimes(quantity: Int): Int {
        val price: Int = this.price().orElse(0)

        return price.times(quantity)
    }
}
