package scripts.wrBarrows.player

enum class BarrowsBrother(
    val brotherName: String,
    val prayer: ProtectionPrayer?
) {
    DHAROCK("Dharok the Wretched", ProtectionPrayer.MELEE),
    AHRIM("Ahrim the Blighted", ProtectionPrayer.MAGIC),
    KARIL("Karil the Tainted", ProtectionPrayer.RANGED),
    TORAG("Torag the Corrupted", null),
    GUTHAN("Guthan the Infested", null),
    VERAC("Verac the Defiled", null)
}