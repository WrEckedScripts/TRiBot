package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore
import scripts.wrBlastFurnace.gui.Settings

data class SteelBar(
    override var states: MutableMap<String, Boolean> = mutableMapOf(
        //TODO, we need to dynamically determine the states, so both flows work
        // Now, it's hard-coded to only work with coal bags in the new smeltNode
//        "PROCESS_SECONDARY" to false,
//        "PROCESS_BASE" to true,
//        "COLLECT_BARS" to true,
//        "BANK_BARS" to true
        "PREFILL_COAL" to false,
        "PREPARE_ORES" to true,
        "PROCESS_ORES" to true,
        "COLLECT_BARS" to true,
        "BANK_BARS" to true,
    )
) : MeltableBar {
    override fun bar(): Bar {
        return Bar("Steel bar", 2353)
    }

    override fun baseOre(): Ore {
        var quantity = 28
        if (Settings.coalBagChecked) {
            quantity = 27
        }

        return Ore("Iron ore", quantity, 440)
    }

    override fun secondaryOre(): Ore {
        var quantity = 28
        if (Settings.coalBagChecked)
            quantity = 27

        return Ore("Coal", quantity, 453)
    }
}