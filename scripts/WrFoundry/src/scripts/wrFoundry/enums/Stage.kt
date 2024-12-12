package scripts.wrFoundry.enums

enum class Stage(
    val displayName: String,
    val heat: Heat,
) {
    TRIP_HAMMER("Hammer", Heat.HIGH),
    GRINDSTONE("Grind", Heat.MED),
    POLISHING_WHEEL("Polish", Heat.LOW);
}