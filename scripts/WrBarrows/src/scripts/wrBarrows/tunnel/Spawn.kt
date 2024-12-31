package scripts.wrBarrows.tunnel

import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.WorldTile

//TODO, grab a list of all doors we should pass
// We'll figure out querying for doors on these area's later.
enum class Spawn(val area: Area, val doors: List<Area>) {
    NORTH_WEST(
        Area.fromRectangle(
            WorldTile(3529, 9717, 0), WorldTile(3540, 9706, 0)
        ),
        listOf(
            Area.fromRectangle(WorldTile(3533, 9706, 0), WorldTile(3536, 9705, 0)),
            Area.fromRectangle(WorldTile(3533, 9701, 0), WorldTile(3536, 9700, 0)),
            Area.fromRectangle(WorldTile(3539, 9696, 0), WorldTile(3541, 9693, 0)),
            Area.fromRectangle(WorldTile(3544, 9696, 0), WorldTile(3546, 9693, 0)),
        )
    ),
    NORTH_EAST(
        Area.fromRectangle(
            WorldTile(3563, 9717, 0), WorldTile(3574, 9706, 0)
        ),
        listOf(
            Area.fromRectangle(WorldTile(3567, 9706, 0), WorldTile(3570, 9705, 0)),
            Area.fromRectangle(WorldTile(3567, 9701, 0), WorldTile(3570, 9700, 0)),
            Area.fromRectangle(WorldTile(3561, 9696, 0), WorldTile(3563, 9693, 0)),
            Area.fromRectangle(WorldTile(3556, 9696, 0), WorldTile(3558, 9693, 0))
        )
    ),
    SOUTH_WEST(
        Area.fromRectangle(
            WorldTile(3528, 9683, 0), WorldTile(3540, 9672, 0)
        ),
        listOf(
            Area.fromRectangle(WorldTile(3533, 9684, 0), WorldTile(3536, 9683, 0)),
            Area.fromRectangle(WorldTile(3533, 9689, 0), WorldTile(3536, 9688, 0)),
            Area.fromRectangle(WorldTile(3539, 9696, 0), WorldTile(3541, 9693, 0)),
            Area.fromRectangle(WorldTile(3544, 9696, 0), WorldTile(3546, 9693, 0)),
        )
    ),
    SOUTH_EAST(
        Area.fromRectangle(
            WorldTile(3563, 9683, 0), WorldTile(3574, 9672, 0)
        ),
        listOf(
            Area.fromRectangle(WorldTile(3567, 9684, 0), WorldTile(3570, 9683, 0)),
            Area.fromRectangle(WorldTile(3567, 9689, 0), WorldTile(3570, 9688, 0)),
            Area.fromRectangle(WorldTile(3561, 9696, 0), WorldTile(3563, 9693, 0)),
            Area.fromRectangle(WorldTile(3556, 9696, 0), WorldTile(3558, 9693, 0))
        )
    )

}