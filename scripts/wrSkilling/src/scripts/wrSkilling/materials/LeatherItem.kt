package scripts.wrSkilling.materials

enum class LeatherItem(
    val itemName: String,
    val minLevel: Int,
    val maxLevel: Int
) {
    LEATHER_GLOVES("Leather gloves", 1, 7),
    LEATHER_BOOTS("Leather boots", 7, 9),
    LEATHER_COWL("Leather cowl", 9, 11),
    LEATHER_VAMBRACES("Leather vambraces", 11, 14),
    LEATHER_BODY("Leather body", 14, 16)
}