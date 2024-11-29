package scripts.wrFoundry.enums

enum class Stage(
    val displayName: String,
    val heat: Heat,
    val progressPerAction: Int,
    val heatChange: Int,
    val distanceToLava: Int,
    val distanceToWaterfall: Int
) {
    TRIP_HAMMER("Hammer", Heat.HIGH, 20, -25, 4, 14),
    GRINDSTONE("Grind", Heat.MED, 10, 15, 7, 19),
    POLISHING_WHEEL("Polish", Heat.LOW, 10, -17, 12, 10);

    fun isHeating(): Boolean {
        return heatChange > 0
    }

    fun isCooling(): Boolean {
        return heatChange < 0
    }

}