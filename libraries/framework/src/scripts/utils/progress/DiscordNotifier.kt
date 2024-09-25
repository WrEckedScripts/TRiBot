package scripts.utils.progress

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbed.*
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
        this.logger = log
    }

    private fun shouldSend(force: Boolean = false): Boolean {
        if (force) {
            return true
        }

        if (this.lastSent == null) {
            return true
        }

        val currentTime = System.currentTimeMillis()
        val nextTime = 60 * 15_000 // 15 minutes in milliseconds

        return (currentTime - this.lastSent!!) >= nextTime
    }

    //https://github.com/MinnDevelopment/discord-webhooks
    //https://discord.com/safety/using-webhooks-and-embeds
    //https://chatgpt.com/c/370616b8-db21-48c5-90ba-f87d5aee3789
    fun messageBuilder(message: String, file: File? = null): WebhookMessageBuilder {
        val content: String = ScriptRuntimeInfo.getScriptName().plus(" ".plus(message))

        val timestamp: OffsetDateTime? = OffsetDateTime.now()
        val color: Int = 0x1E90FF

        val author = EmbedAuthor(
            ScriptRuntimeInfo.getScriptName(),
            "https://avatars.githubusercontent.com/u/173479807?s=96&v=4",
            null
        )

        val footer = EmbedFooter(
            "Keep on earning!",
            null
        )

        val title = EmbedTitle(
            "Going at it:",
            null,
        )

        val description = "Your account (${MyPlayer.getUsername()}) is working hard at the Blast Furnace!"
        val thumbnailUrl = null
        val imageUrl = "attachment://progress-update.png"

        val fields: List<WebhookEmbed.EmbedField> = emptyList()

        val webhookEmbed = WebhookEmbed(
            timestamp,
            color,
            description,
            thumbnailUrl,
            imageUrl,
            footer,
            title,
            author,
            fields
        )

        var builder = WebhookMessageBuilder()
            .setUsername(MyPlayer.getUsername())
            .setAvatarUrl("https://avatars.githubusercontent.com/u/173479807?s=96&v=4")
            .addEmbeds(webhookEmbed)

        if (file != null) {
            builder = builder.addFile("progress-update.png", file)
        }

        return builder
    }

    fun screenshot(force: Boolean = false): Unit {
        if (!this.shouldSend(force)) {
            return
        }

        val screenshot = Screenshot.captureWithPaint()

        //TODO this now writes an image to the main tribot folder.
        val file = File(Tribot.getDirectory().path, "update.png")

        //todo do we need this?
        ImageIO.write(screenshot, "png", file)

        WebhookClient.withUrl(
            "https://discord.com/api/webhooks/1279066926953402419/VBj8I3sB4Scj73MoV_p1Ei-uUGOCW-b09swi6gKMNVC_o1MsL_eQDkOuyTH47-3a38w-"
        ).send(this.messageBuilder("Update", file).build())

        this.lastSent = System.currentTimeMillis()
    }

    fun send(message: String): Unit {
        WebhookClient.withUrl(
            "https://discord.com/api/webhooks/1279066926953402419/VBj8I3sB4Scj73MoV_p1Ei-uUGOCW-b09swi6gKMNVC_o1MsL_eQDkOuyTH47-3a38w-"
        ).send(
            MyPlayer.getUsername()
                .plus(" - ")
                .plus(message)
        )
    }
}