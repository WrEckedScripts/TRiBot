package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore

data class IronBar(
    override val states: MutableMap<String, Boolean> = mutableMapOf(
        "PROCESS_BASE" to false,
        "COLLECT_BARS" to true,
        "BANK_BARS" to true
    )
) : MeltableBar {
    override fun bar(): Bar {
        return Bar("Iron bar", 2351)
    }

    override fun baseOre(): Ore {
        return Ore("Iron ore", 28, 440)
    }

    override fun secondaryOre(): Ore? {
        return null
    }
}