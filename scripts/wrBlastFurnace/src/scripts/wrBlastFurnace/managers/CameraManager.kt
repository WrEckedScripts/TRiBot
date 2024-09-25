package scripts.wrBlastFurnace.managers

import org.tribot.script.sdk.Camera
import org.tribot.script.sdk.GameTab
import org.tribot.script.sdk.util.TribotRandom
import scripts.utils.Logger

class CameraManager(val logger: Logger) {

    fun initialize(){
        // Set to mouse scrolling
        Camera.setZoomMethod(Camera.ZoomMethod.MOUSE_SCROLL) //todo user pref?
        Camera.setRotationMethod(Camera.RotationMethod.MOUSE) //todo user pref?
        logger.info("[CameraManager] - set zooming method to 'mouse scrolling'")

        this.randomize()
    }

    private fun setZoom(): Unit {
        val randomZoom = TribotRandom.normal(2.23, 1.25)
        Camera.setZoomPercent(randomZoom)
        logger.info("[CameraManager] - set zoom % to ${randomZoom}")
    }

    private fun setAngle(): Unit {
        val randomAngle = TribotRandom.normal(90, 4)
        Camera.setAngle(randomAngle)
        logger.info("[CameraManager] - set angle to ${randomAngle}")
    }

    private fun setRotation(): Unit {
        val randomRotation = TribotRandom.normal(285, 15)
        Camera.setRotation(randomRotation)
        logger.info("[CameraManager] - set rotation to ${randomRotation}")
    }

    fun randomize(
        zoom: Boolean = true,
        angle: Boolean = true,
        rotation: Boolean = true
    ): Unit {
        if (zoom) {
            this.setZoom()
        }
        if (angle) {
            this.setAngle()
        }
        if (rotation) {
            this.setRotation()
        }

        GameTab.INVENTORY.open()
        logger.info("[CameraManager] - finished camera randomization")
    }
}