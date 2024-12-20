package scripts.wrFoundry.enums

enum class Commission(
    val commission: String,
    val combination: MouldCombination
) {
    BROAD_HEAVY("Broad Heavy", MouldCombination.BROAD_HEAVY),
    BROAD_LIGHT("Broad Light", MouldCombination.BROAD_LIGHT),
    BROAD_SPIKED("Broad Spiked", MouldCombination.BROAD_SPIKED),
    BROAD_FLAT("Broad Flat", MouldCombination.BROAD_FLAT),
    NARROW_HEAVY("Narrow Heavy", MouldCombination.NARROW_HEAVY),
    NARROW_LIGHT("Narrow Light", MouldCombination.NARROW_LIGHT),
    NARROW_SPIKED("Narrow Spiked", MouldCombination.NARROW_SPIKED),
    NARROW_FLAT("Narrow Flat", MouldCombination.NARROW_FLAT),
    HEAVY_FLAT("Heavy Flat", MouldCombination.HEAVY_FLAT),
    HEAVY_SPIKED("Heavy Spiked", MouldCombination.HEAVY_SPIKED),
    LIGHT_SPIKED("Light Spiked", MouldCombination.LIGHT_SPIKED),
    LIGHT_FLAT("Light Flat", MouldCombination.LIGHT_FLAT);

    companion object {
        fun fromString(value: String): Commission? {
            return values().find { it.commission == value }
        }
    }
}
