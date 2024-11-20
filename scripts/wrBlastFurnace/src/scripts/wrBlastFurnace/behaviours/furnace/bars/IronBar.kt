package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore

object IronBar : MeltableBar {
    override fun states(): MutableMap<String, Boolean> {
        return mutableMapOf(
            "PROCESS_BASE" to false,
            "COLLECT_BARS" to true,
            "BANK_BARS" to true
        )
    }

    override fun bar(): Bar {
        return Bar("Iron bar", 2351)
    }

    override fun baseOre(): Ore {
        return Ore("Iron ore", this.quantity(), 440)
    }

    override fun secondaryOre(): Ore? {
        return null
    }

    override fun quantity(): Int {
        return 28
    }
}