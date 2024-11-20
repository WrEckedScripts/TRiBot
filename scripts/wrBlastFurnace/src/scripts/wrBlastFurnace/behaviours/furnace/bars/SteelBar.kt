package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore
import scripts.wrBlastFurnace.gui.Settings

object SteelBar : MeltableBar {
    override fun states(): MutableMap<String, Boolean> {
        if (Settings.coalBagChecked) {
            return mutableMapOf(
                "PREFILL_COAL" to false,
                "PREPARE_ORES" to true,
                "PROCESS_ORES" to true,
                "COLLECT_BARS" to true,
                "BANK_BARS" to true,
            )
        }

        return mutableMapOf(
            "PROCESS_SECONDARY" to false,
            "PROCESS_BASE" to true,
            "COLLECT_BARS" to true,
            "BANK_BARS" to true
        )
    }

    override fun bar(): Bar {
        return Bar("Steel bar", 2353)
    }

    override fun baseOre(): Ore {
        return Ore("Iron ore", this.quantity(), 440)
    }

    override fun secondaryOre(): Ore {

        return Ore("Coal", this.quantity(), 453)
    }

    override fun quantity(): Int {
        return if (Settings.coalBagChecked) {
            27
        } else {
            28
        }
    }
}