package scripts.utils.progress.webhook

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookMessage
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Screenshot
import org.tribot.script.sdk.Tribot
import org.tribot.script.sdk.script.ScriptRuntimeInfo
import scripts.utils.Logger
import java.io.File
import java.time.OffsetDateTime
import javax.imageio.ImageIO

object DiscordNotifier {
    /**
     * Configuration
     */
    private var url: String? = null
    private var interval: Int = 15
    private var logger: Logger? = null

    /**
     * State
     */
    private var enabled: Boolean = false
    private var lastSent: Long? = null

    fun initLogger(log: Logger) {
        this.logger = log
    }

    fun initConfig(url: String, interval: Int) {
        this.url = url
        this.interval = interval

        if (this.url == "") {
            this.logger?.error("[Discord] - Failed to parse webhook url from GUI, we're not going to send updates")
        } else {
            this.logger?.info("[Discord] - Enabled Discord notifications, we will be sending updates")
            this.enabled = true
        }
    }

    /**
     * Notify the known webhook, regarding the current progression
     */
    fun notify(force: Boolean = false, message: String? = null, color: Int? = null) {
        this.sendScreenshot(force, message, color)
    }

    /**
     * Determine if we're forcing a screenshot for sending, or that we're preventing it so we don't spam the webhook
     */
    private fun shouldSend(force: Boolean = false): Boolean {
        if (!this.enabled) {
            return false
        }

        if (force) {
            return true
        }

        if (this.lastSent == null) {
            return true
        }

        val currentTime = System.currentTimeMillis()
        val nextTime = 60 * (this.interval * 1_000)

        return (currentTime - this.lastSent!!) >= nextTime
    }

    private fun messageEmbedWithScreenshot(message: String?, color: Int?): WebhookEmbed {
        return WebhookEmbedBuilder()
            .setDescription(message ?: "Progress update on (${MyPlayer.getUsername()})")
            .setAuthor(
                EmbedAuthor(
                    ScriptRuntimeInfo.getScriptName(),
                    "https://avatars.githubusercontent.com/u/173479807?s=96&v=4",
                    null
                )
            )
            .setTimestamp(OffsetDateTime.now())
            .setColor(color ?: 0x1E90FF)
            .setImageUrl("attachment://progressUpdate.png")
            .build()
    }

    private fun message(file: File?, message: String?, color: Int?): WebhookMessage {
        val messageEmbed = this.messageEmbedWithScreenshot(message, color)

        var builder = WebhookMessageBuilder()
            .setUsername(MyPlayer.getUsername())
            .setAvatarUrl("https://avatars.githubusercontent.com/u/173479807?s=96&v=4")
            .addEmbeds(messageEmbed)

        if (file != null) {
            builder = builder.addFile("progressUpdate.png", file)
        }

        return builder.build()
    }

    private fun createScreenshot(): File {
        val screenshot = Screenshot.captureWithPaint()
        val file = File(Tribot.getDirectory().path, "update.png")

        ImageIO.write(screenshot, "png", file)

        return file
    }

    private fun sendScreenshot(force: Boolean = false, message: String?, color: Int?): Unit {
        if (!shouldSend(force)) {
            return
        }

        if (!this.enabled || this.url.equals(null)) {
            return
        }

        WebhookClient.withUrl(
            this.url!!
        ).send(
            message(this.createScreenshot(), message, color)
        )

        this.lastSent = System.currentTimeMillis()
    }
}