package scripts.wrCrafting.gui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import scripts.wrCrafting.gui.methods.CraftingMethod
import scripts.wrCrafting.gui.methods.DefaultOption
import scripts.wrCrafting.gui.methods.Gemstones
import scripts.wrCrafting.gui.methods.MoltenGlass

class ScriptSettings {
    var method: String by mutableStateOf("")
    var gemMaterial: String by mutableStateOf("")

    var batchBuy: Int by mutableStateOf(1000)
    var selling: Boolean by mutableStateOf(false)
    var stopLevel: Int by mutableStateOf(99)

    fun getMethods(): Map<String, CraftingMethod> {
        return mapOf(
            "" to DefaultOption(),
            "cutting_gems" to Gemstones(),
            "furnace_molten" to MoltenGlass()
        )
    }
}