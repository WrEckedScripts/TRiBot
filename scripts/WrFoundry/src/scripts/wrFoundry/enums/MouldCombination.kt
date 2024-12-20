package scripts.wrFoundry.enums

enum class MouldCombination(
    val forte: String,
    val blade: String,
    val tip: String
) {
    BROAD_HEAVY(
        "Medusa Ricasso",
        "Medusa Blade",
        "Saw Tip"
    ),
    BROAD_LIGHT(
        "Medusa Ricasso",
        "Medusa Blade",
        "Chopper Tip"
    ),
    BROAD_SPIKED(
        "Medusa Ricasso",
        "Medusa Blade",
        "Saw Tip"
    ),
    BROAD_FLAT(
        "Medusa Ricasso",
        "Medusa Blade",
        "Saw Tip"
    ),
    NARROW_HEAVY(
        "Serrated Forte",
        "Gladius Edge",
        "Gladius Point"
    ),
    NARROW_LIGHT(
        "Serpent Ricasso",
        "Stiletto Blade",
        "Serpent's Fang"
    ),
    NARROW_SPIKED(
        "Serrated Forte",
        "Stiletto Blade",
        "Serpent's Fang"
    ),
    NARROW_FLAT(
        "Serpent Ricasso",
        "Stiletto Blade",
        "Gladius Point"
    ),
    HEAVY_FLAT(
        "Medusa Ricasso",
        "Gladius Edge",
        "Gladius Point"
    ),
    HEAVY_SPIKED(
        "Serrated Forte",
        "Gladius Edge",
        "Saw Tip"
    ),
    LIGHT_SPIKED(
        "Serpent Ricasso",
        "Saw Blade",
        "Serpent's Fang"
    ),
    LIGHT_FLAT(
        "Serpent Ricasso",
        "Stiletto Blade",
        "Gladius Point"
    );
}
