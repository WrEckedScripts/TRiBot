package scripts.wrBarrows.player

import org.tribot.script.sdk.GameState
import scripts.utils.Logger

enum class BarrowsGameData(val bitId: Int) {
    //TODO guessing 0/1 values
    BARROWS_KILLED_AHRIM(457),
    BARROWS_KILLED_DHAROK(458),
    BARROWS_KILLED_GUTHAN(459),
    BARROWS_KILLED_KARIL(460),
    BARROWS_KILLED_TORAG(461),
    BARROWS_KILLED_VERAC(462),

    BARROWS_REWARD_POTENTIAL(463), //TODO figure out what kind of value?
    BARROWS_NPCS_SLAIN(464); // TODO should be a count?

    fun get(): Int {
        Logger("Varbits").error("${this.bitId} = ${GameState.getVarbit(this.bitId)}")
        return GameState.getVarbit(this.bitId)
    }
}