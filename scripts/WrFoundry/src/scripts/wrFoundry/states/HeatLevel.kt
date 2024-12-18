package scripts.wrFoundry.states

import org.tribot.script.sdk.GameState
import scripts.wrFoundry.enums.Heat

object HeatLevel {
    private val heatBit = 13948

    var current: Heat? = null

    fun get(): Heat? {
        val heatValue = GameState.getVarbit(heatBit)
        when (heatValue) {
            in Heat.HIGH.min..Heat.HIGH.max -> Heat.HIGH
            in Heat.MED.min..Heat.MED.max -> Heat.MED
            in Heat.LOW.min..Heat.LOW.max -> Heat.LOW
            else -> null
        }.also {
            this.current = it
        }

        return this.current
    }

    fun getRawValue(): Int {
        return GameState.getVarbit(heatBit)
    }
}

//heat flow
// - green | polish = cools
// - orange | grind = heats
// - red | hammer = cools

// test
// 938 - 730 = hot
// 600 - 400 = med
// 260 - 65 = low

// Widget 754 child count 83 = main overlay block
//  Child 19 - = LOW heat block
//  Child 20 - = MED heat block
//  Child 21 - = HIGH heat block
// Child 75 - is bottom stage overlay

// Heat indicator - Child 74


// Stage - Texture id 4443 looks like grindstone (orange)
// Stage - Texture id 4442 looks like hammer (red)
// Stage - Texture id

// Stage indicator Texture: 4445 - Child 78 - Relative position could be useful
// example - Relative Position 294,5 = 5th stage (red hammering)
// - red hammering in that stage = Relative position 296, it could be a bit off, due to cursor center?
// example2 - Cursor 365,5 = right before stage 6 (5 highlighted)
// stage 6 starts at 368 (Child 59)
