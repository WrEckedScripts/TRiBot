package scripts.wrBarrows.player

enum class BarrowsBrother(
    val brotherName: String,
    val prayer: ProtectionPrayer?,
    val varbit: BarrowsGameData
) {
    DHAROCK("Dharok the Wretched", ProtectionPrayer.MELEE, BarrowsGameData.BARROWS_KILLED_DHAROK),
    AHRIM("Ahrim the Blighted", ProtectionPrayer.MAGIC, BarrowsGameData.BARROWS_KILLED_AHRIM),
    KARIL("Karil the Tainted", ProtectionPrayer.RANGED, BarrowsGameData.BARROWS_KILLED_KARIL),
    TORAG("Torag the Corrupted", null, BarrowsGameData.BARROWS_KILLED_TORAG),
    GUTHAN("Guthan the Infested", null, BarrowsGameData.BARROWS_KILLED_GUTHAN),
    VERAC("Verac the Defiled", null, BarrowsGameData.BARROWS_KILLED_VERAC)
}