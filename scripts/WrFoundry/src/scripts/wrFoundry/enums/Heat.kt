package scripts.wrFoundry.enums

enum class Heat(val displayName: String, val min: Int, val max: Int) {
    LOW("Low", 65, 265),
    MED("Medium", 400, 600),
    HIGH("High", 735, 935)
}