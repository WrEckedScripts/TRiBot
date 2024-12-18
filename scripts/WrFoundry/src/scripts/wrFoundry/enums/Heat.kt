package scripts.wrFoundry.enums

enum class Heat(val displayName: String, val min: Int, val max: Int) {
    // To allow for any sort of resource, we should calculate these bounds,
    // perhaps coordinates reflect these? Or some other Widget attribute?
    LOW("Low", 75, 265), // TODO, can we resolve these bounds??
    MED("Medium", 400, 580), // TODO, can we resolve these bounds??
    HIGH("High", 735, 935) // TODO, can we resolve these bounds??
}