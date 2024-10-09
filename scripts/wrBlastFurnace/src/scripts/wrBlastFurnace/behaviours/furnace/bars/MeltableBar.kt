package scripts.wrBlastFurnace.behaviours.furnace.bars

import scripts.wrBlastFurnace.banking.materials.Bar
import scripts.wrBlastFurnace.banking.materials.Ore

interface MeltableBar {
    val states: MutableMap<String, Boolean>

    fun bar(): Bar
    fun baseOre(): Ore
    fun secondaryOre(): Ore?
}