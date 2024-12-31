package scripts.wrBarrows.player

import org.tribot.script.sdk.types.Area
import org.tribot.script.sdk.types.WorldTile

/**
 * Contains all area's where our player handles the Barrows activity
 */
enum class BarrowsArea(val surface: Area, val crypt: Area) {
    BARROWS(
        Area.fromPolygon(
            WorldTile(3547, 3314, 0), WorldTile(3584, 3314, 0),
            WorldTile(3584, 3267, 0), WorldTile(3546, 3267, 0)
        ),
        Area.fromPolygon(
            WorldTile(3522, 9724, 0), WorldTile(3581, 9724, 0),
            WorldTile(3581, 9665, 0), WorldTile(3522, 9665, 0)
        )
    ),
    DHAROCK_AREA(
        Area.fromPolygon(
            WorldTile(3574, 3301, 0), WorldTile(3578, 3301, 0),
            WorldTile(3578, 3297, 0), WorldTile(3574, 3297, 0)
        ),
        Area.fromPolygon(
            WorldTile(3549, 9719, 3), WorldTile(3560, 9719, 3),
            WorldTile(3560, 9710, 3), WorldTile(3549, 9710, 3)
        )
    ),
    AHRIM_AREA(
        Area.fromPolygon(
            WorldTile(3563, 3291, 0), WorldTile(3567, 3291, 0),
            WorldTile(3567, 3287, 0), WorldTile(3563, 3287, 0)
        ),
        Area.fromPolygon(
            WorldTile(3550, 9704, 3), WorldTile(3561, 9704, 3),
            WorldTile(3561, 9694, 3), WorldTile(3550, 9694, 3)
        )
    ),
    KARIL_AREA(
        Area.fromPolygon(
            WorldTile(3564, 3277, 0), WorldTile(3567, 3277, 0),
            WorldTile(3567, 3274, 0), WorldTile(3564, 3274, 0)
        ),
        Area.fromPolygon(
            WorldTile(3545, 9688, 3), WorldTile(3557, 9688, 3),
            WorldTile(3557, 9678, 3), WorldTile(3545, 9678, 3)
        )
    ),
    TORAG_AREA(
        Area.fromPolygon(
            WorldTile(3552, 3285, 0), WorldTile(3556, 3285, 0),
            WorldTile(3556, 3281, 0), WorldTile(3552, 3281, 0),
        ),
        Area.fromPolygon(
            WorldTile(3564, 9692, 3), WorldTile(3575, 9692, 3),
            WorldTile(3575, 9682, 3), WorldTile(3564, 9682, 3)
        )
    ),
    GUTHAN_AREA(
        Area.fromPolygon(
            WorldTile(3575, 3284, 0), WorldTile(3579, 3284, 0),
            WorldTile(3579, 3280, 0), WorldTile(3575, 3280, 0)
        ),
        Area.fromPolygon(
            WorldTile(3533, 9708, 3), WorldTile(3545, 9708, 3),
            WorldTile(3545, 9699, 3), WorldTile(3533, 9699, 3)
        )
    ),
    VERAC_AREA(
        Area.fromPolygon(
            WorldTile(3555, 3300, 0), WorldTile(3559, 3300, 0),
            WorldTile(3559, 3296, 0), WorldTile(3555, 3296, 0)
        ),
        Area.fromPolygon(
            WorldTile(3568, 9710, 3), WorldTile(3579, 9710, 3),
            WorldTile(3579, 9702, 3), WorldTile(3568, 9702, 3)
        )
    )
}