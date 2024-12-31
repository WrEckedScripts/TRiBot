package scripts.wrBarrows.player

import org.tribot.script.sdk.Prayer

enum class ProtectionPrayer(val protectionPrayer: Prayer) {
    MELEE(Prayer.PROTECT_FROM_MELEE),
    RANGED(Prayer.PROTECT_FROM_MISSILES),
    MAGIC(Prayer.PROTECT_FROM_MAGIC)
}
