package scripts.wrFoundry.enums

enum class Stage(
    val displayName: String,
    val heat: Heat,
    val temperatureDirection: TemperatureDirection
) {
    TRIP_HAMMER("Hammer", Heat.HIGH, TemperatureDirection.COOLING),
    GRINDSTONE("Grind", Heat.MED, TemperatureDirection.HEATING),
    POLISHING_WHEEL("Polish", Heat.LOW, TemperatureDirection.COOLING);
}