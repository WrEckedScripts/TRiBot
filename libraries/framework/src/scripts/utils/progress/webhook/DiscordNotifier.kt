package scripts.utils.progress.webhook

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter
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
    private var lastSent: Long? = null

    private var logger: Logger? = null

    fun initLogger(log: Logger) {
        logger = log
    }

    /**
     * Notify the known webhook, regarding the current progression
     */
    fun notify(force: Boolean = false) {
        this.sendScreenshot(force)
    }

    /**
     * Determine if we're forcing a screenshot for sending, or that we're preventing it so we don't spam our discord
     * Allows for timely updates, now HARDCODED to 15 minutes. But will need to be adjustable.
     * @todo, adjustable webhook url + timing
     *  - where the timing interval is in minutes
     */
    private fun shouldSend(force: Boolean = false): Boolean {
        if (force) {
            return true
        }

        if (lastSent == null) {
            return true
        }

        val currentTime = System.currentTimeMillis()
        val nextTime = 60 * 2_000 // 15 minutes in milliseconds

        return (currentTime - lastSent!!) >= nextTime
    }

    private fun embed(): WebhookEmbed {
        return WebhookEmbedBuilder()
            .setDescription("Your account (${MyPlayer.getUsername()}) is grinding at the Blast Furnace!")
            .setAuthor(
                EmbedAuthor(
                    ScriptRuntimeInfo.getScriptName(),
                    "https://avatars.githubusercontent.com/u/173479807?s=96&v=4",
                    null
                )
            )
            .setFooter(
                EmbedFooter(
                    "Go hard or go home!",
                    null
                )
            )
            .setTimestamp(OffsetDateTime.now())
            .setColor(0x1E90FF)
            .setImageUrl("attachment://wrBlastFurnaceUpdate.png")
            .build()
    }

    private fun message(file: File?): WebhookMessage {
        val embed = this.embed()

        var builder = WebhookMessageBuilder()
            .setUsername(MyPlayer.getUsername())
            .setAvatarUrl("https://avatars.githubusercontent.com/u/173479807?s=96&v=4")
            .addEmbeds(embed)

        if (file != null) {
            builder = builder.addFile("wrBlastFurnaceUpdate.png", file)
        }

        return builder.build()
    }

    private fun createScreenshot(): File {
        val screenshot = Screenshot.captureWithPaint()
        val file = File(Tribot.getDirectory().path, "update.png")

        ImageIO.write(screenshot, "png", file)

        return file
    }

    private fun sendScreenshot(force: Boolean = false): Unit {
        if (!shouldSend(force)) {
            return
        }

        WebhookClient.withUrl(
            "https://discord.com/api/webhooks/1279066926953402419/VBj8I3sB4Scj73MoV_p1Ei-uUGOCW-b09swi6gKMNVC_o1MsL_eQDkOuyTH47-3a38w-"
        ).send(
            message(this.createScreenshot())
        )

        lastSent = System.currentTimeMillis()
    }
}