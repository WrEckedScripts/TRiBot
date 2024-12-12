package scripts.wrFoundry.enums

enum class InteractableMachine(val objectName: String, val heat: Heat) {
    HAMMER("Trip hammer", Heat.HIGH),
    GRINDSTONE("Grindstone", Heat.MED),
    POLISHING_WHEEL("Polishing wheel", Heat.LOW)
}