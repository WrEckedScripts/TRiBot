package scripts.wrCrafting.gui.methods

import scripts.wrCrafting.gui.methods.materials.*

class Gemstones : CraftingMethod() {
    override val name: String = "Gemstones (cutting)"
    override val hasMaterialOptions = true

    fun getMaterials(): Map<String, GemstoneInterface> {
        return mapOf(
            "sapphire" to Sapphire(),
            "emerald" to Emerald(),
            "ruby" to Ruby(),
            "diamond" to Diamond(),
            "dragonstone" to Dragonstone()
        )
    }
}