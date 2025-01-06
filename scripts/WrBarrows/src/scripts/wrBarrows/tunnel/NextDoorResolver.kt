package scripts.wrBarrows.tunnel

import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.painting.Painting
import org.tribot.script.sdk.types.GameObject
import org.tribot.script.sdk.types.WorldTile
import org.tribot.script.sdk.walking.LocalWalking
import java.awt.Color
import java.awt.Graphics

class NextDoorResolver(val doors: MutableList<GameObject>) {
    fun paint(tile: WorldTile, color: Color) {
        if (!tile.isRendered) {
            return
        }

        Painting.addPaint { g: Graphics ->
            g.color = color
            g.drawPolygon(tile.bounds.get())
            g.drawString(tile.distance().toString(), tile.x, tile.y)
        }
    }

    fun furthestReachableDoor(): GameObject {
        this.doors.sortByDescending {
            it.distance()
        }

        return this.doors.last {
            this.paint(it.tile, Color.MAGENTA)
            this.getPath(it.tile)
        }
    }

    fun getPath(tile: WorldTile): Boolean {
        val path = LocalWalking.Map.builder().source(MyPlayer.getTile())
            .build()

        Painting.addPaint { g: Graphics ->
            g.color = Color.PINK

            path.getPath(tile).forEach {
                g.drawPolygon(it.tile.bounds.get())
            }
        }

        return path.getPath(tile).isNotEmpty()
    }
}