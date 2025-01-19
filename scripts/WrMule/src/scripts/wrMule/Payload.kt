package scripts.wrMule

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.WorldHopper

class Payload {
    val action = "trade"
    val player = MyPlayer.getUsername()
    val world = WorldHopper.getCurrentWorld()
    val success = Login.isLoggedIn()
    val coordinates = mapOf(
        "x" to MyPlayer.getTile().x,
        "y" to MyPlayer.getTile().y,
        "plane" to MyPlayer.getTile().plane,
    )
}