package scripts.wrBarrows.rooms

data class RoomState(
    val room: Room,
    var isCompleted: Boolean = false,
    var isTunnel: Boolean = false
)
