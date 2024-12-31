package scripts.wrBarrows.rooms

import scripts.wrBarrows.player.BarrowsArea
import scripts.wrBarrows.player.BarrowsBrother


/**
 * Enum class that contains each different crypt specifics
 * Like:
 * - The Brother
 * - Surface area to dig
 */
enum class Room(
    val brother: BarrowsBrother,
    val area: BarrowsArea,
) {
    // Prayer preferred
    DHAROCK(
        BarrowsBrother.DHAROCK,
        BarrowsArea.DHAROCK_AREA
    ),

    AHRIM(
        BarrowsBrother.AHRIM,
        BarrowsArea.AHRIM_AREA
    ),

    KARIL(
        BarrowsBrother.KARIL,
        BarrowsArea.KARIL_AREA
    ),

    // Food preferred
    TORAG(
        BarrowsBrother.TORAG,
        BarrowsArea.TORAG_AREA
    ),

    GUTHAN(
        BarrowsBrother.GUTHAN,
        BarrowsArea.GUTHAN_AREA
    ),

    VERAC(
        BarrowsBrother.VERAC,
        BarrowsArea.VERAC_AREA
    )
}