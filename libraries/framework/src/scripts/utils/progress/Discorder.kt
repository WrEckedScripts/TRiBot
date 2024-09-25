package scripts.utils.progress

import club.minnced.discord.webhook.WebhookClient
import org.tribot.script.sdk.Screenshot
import org.tribot.script.sdk.Tribot
import scripts.utils.Logger
import java.io.File
import javax.imageio.ImageIO

object Discorder {
    private var lastSent: Long? = null

    private var logger: Logger? = null

    fun initLogger(log: Logger) {
        this.logger = log
    }

    private fun shouldSend(): Boolean {
        if (this.lastSent == null) {
            return true
        }

        val currentTime = System.currentTimeMillis()
        val nextTime = 60 * 15_000 // 15 minutes in milliseconds

        return (currentTime - this.lastSent!!) >= nextTime
    }


    fun screenshot(): Unit {
        if (!this.shouldSend()) {
            return
        }

        val screenshot = Screenshot.captureWithPaint()

        val file = File(Tribot.getDirectory().path, "progress.png")

        //todo do we need this?
        ImageIO.write(screenshot, "png", file)

        WebhookClient.withUrl(
            "https://discord.com/api/webhooks/1279066926953402419/VBj8I3sB4Scj73MoV_p1Ei-uUGOCW-b09swi6gKMNVC_o1MsL_eQDkOuyTH47-3a38w-"
        ).send(file)

        this.lastSent = System.currentTimeMillis()
    }

    fun send(message: String): Unit {
        WebhookClient.withUrl(
            "https://discord.com/api/webhooks/1279066926953402419/VBj8I3sB4Scj73MoV_p1Ei-uUGOCW-b09swi6gKMNVC_o1MsL_eQDkOuyTH47-3a38w-"
        ).send(message)
    }
}