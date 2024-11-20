package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore

object BronzeBar : MeltableBar {
    override fun states(): MutableMap<String, Boolean> {
        return mutableMapOf(
            "PROCESS_SECONDARY" to false,
            "PROCESS_BASE" to true,
            "COLLECT_BARS" to true,
            "BANK_BARS" to true
        )
    }

    override fun bar(): Bar {
        return Bar("Bronze bar", 2349)
    }

    override fun baseOre(): Ore {
        return Ore("Tin ore", this.quantity(), 438)
    }

    override fun secondaryOre(): Ore {
        return Ore("Copper ore", this.quantity(), 436)
    }

    override fun quantity(): Int {
        return 28
    }
}