package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore

data class SteelBar(
    override val states: MutableMap<String, Boolean> = mutableMapOf(
        "PROCESS_SECONDARY" to false,
        "PROCESS_BASE" to true,
        "COLLECT_BARS" to true,
        "BANK_BARS" to true
    )
) : MeltableBar {
    override fun bar(): Bar {
        return Bar("Steel bar", 2353)
    }

    override fun baseOre(): Ore {
        return Ore("Iron ore", 28, 440)
    }

    override fun secondaryOre(): Ore {
        return Ore("Coal", 28, 453)
    }
}