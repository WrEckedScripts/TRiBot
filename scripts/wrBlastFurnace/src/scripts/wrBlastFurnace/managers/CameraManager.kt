package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.Camera
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger
import scripts.wrBlastFurnace.gui.Settings

class CameraManager(val logger: Logger) {

    fun initialize() {
        Camera.setZoomMethod(Settings.getZoomMethod())
        Camera.setRotationMethod(Settings.getRotateMethod())

        this.randomize()
    }

    private fun setZoom(): Double {
        val randomZoom = TribotRandom.normal(2.23, 1.25)
        Camera.setZoomPercent(randomZoom)
        return randomZoom
    }

    private fun setAngle(): Int {
        val randomAngle = TribotRandom.normal(90, 4)
        Camera.setAngle(randomAngle)
        return randomAngle
    }

    private fun setRotation(): Int {
        val randomRotation = TribotRandom.normal(285, 15)
        Camera.setRotation(randomRotation)
        return randomRotation
    }

    fun randomize(
        zoom: Boolean = true,
        angle: Boolean = true,
        rotation: Boolean = true
    ): Unit {
        var logMessage = "[Camera] - "
        if (zoom) {
            val newZoom: Double = this.setZoom()
            logMessage = logMessage.plus("zoom: ${newZoom} ")
        }

        if (angle) {
            val newAngle = this.setAngle()
            logMessage = logMessage.plus("angle: ${newAngle} ")
        }

        if (rotation) {
            val newRotate = this.setRotation()
            logMessage = logMessage.plus("rotate: ${newRotate} ")
        }

        logger.info(logMessage)
    }
}