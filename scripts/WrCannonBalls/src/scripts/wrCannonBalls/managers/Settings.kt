package scripts.wrCannonBalls.managers

object Settings {

    var mouldId: Int = 4

    fun setToDoubleMould() {
        this.initMouldId(27012)
    }

    fun initMouldId(itemId: Int) {
        this.mouldId = itemId
    }
}