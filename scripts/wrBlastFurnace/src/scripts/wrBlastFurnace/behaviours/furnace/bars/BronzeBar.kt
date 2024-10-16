package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore

data class BronzeBar(
    override val states: MutableMap<String, Boolean> = mutableMapOf(
        "PROCESS_SECONDARY" to false,
        "PROCESS_BASE" to true,
        "COLLECT_BARS" to true,
        "BANK_BARS" to true
    )
) : MeltableBar {
    override fun bar(): Bar {
        return Bar("Bronze bar", 2349)
    }

    override fun baseOre(): Ore {
        return Ore("Tin ore", 28, 438)
    }

    override fun secondaryOre(): Ore {
        return Ore("Copper ore", 28, 436)
    }
}