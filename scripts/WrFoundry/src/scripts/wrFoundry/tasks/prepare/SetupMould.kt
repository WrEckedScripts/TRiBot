package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Widget
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrFoundry.enums.Commission
import kotlin.jvm.optionals.getOrNull

class SetupMould(val commission: Commission) {

    fun execute(): Boolean {
        Logger("[Commission]").warn("Commission: ${this.commission.commission}")
        Logger("[Commission]").warn("Forte: ${this.commission.combination.forte}")
        Logger("[Commission]").warn("Blade: ${this.commission.combination.blade}")
        Logger("[Commission]").warn("Tip: ${this.commission.combination.tip}")

        openMouldInterface()
        Logger("[Commission]").warn("opened...")

        pickCombination()
        Logger("[Commission]").warn("picked..")

        return true
    }

    fun openMouldInterface(): Boolean {
        // Setup
        val mouldObject = Query.gameObjects()
            .nameContains("Mould jig")
            .findFirst()
            .get()

        val interacted = mouldObject.interact("Setup")
        if (interacted == false) {
            mouldObject.interact("Check")
        }

        Waiting.waitUntil {
            val mouldInterface = Query.widgets()
                .inIndexPath(718)
                .findFirst()
                .getOrNull()

            mouldInterface?.isVisible() == true
        }

        Waiting.wait(FatigueResolver.getMilliseconds())

        Logger("[MouldJig]").warn("Mould is visible")
        return true
    }

    fun pickCombination(): Boolean {
        Logger("[PickCombo's]").warn("Let's pick some!")
        this.findWidgetByName("Tips")?.click()

        Waiting.wait(FatigueResolver.getMilliseconds())
        this.findAndScroll(this.commission.combination.tip)

        Waiting.wait(FatigueResolver.getMilliseconds())
        this.findWidgetByName(this.commission.combination.tip)?.click()

        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findWidgetByName("Blades")?.click()
        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findAndScroll(this.commission.combination.blade)
        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findWidgetByName(this.commission.combination.blade)?.click()
        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findWidgetByName("Forte")?.click()
        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findAndScroll(this.commission.combination.forte)
        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findWidgetByName(this.commission.combination.forte)?.click()
        Waiting.wait(FatigueResolver.getMilliseconds())

        val setMould = Query.widgets().inRoots(718)
            .isDepth(2)
            .inIndexPath(718, 34)
            .findFirst()
            .get()
            .click()

        return setMould
    }

    fun findAndScroll(findName: String): Boolean {
        return Waiting.waitUntil {
            var result = false
            val widget = this.findWidgetByName(findName)

            if (widget != null && !widget.isVisible) {
                Waiting.waitUntil(5_000) {
                    widget.scrollTo()
                    widget.isVisible
                }

                result = widget.isVisible
            }

            result
        }
    }

    fun findWidgetByName(findName: String): Widget? {
        // When logging this, it was very slow. Perhaps now without it isn't?
        // Other-wise we need to adjust.
        val result = Query.widgets()
            .inRoots(718)
            .inIndexPath(718, 12)
            .isDepth(3)
            .filter { widget ->
                val widgetName = widget.name.toString()
                val extractedName = extractComponentName(widgetName)
                extractedName == findName
            }
            .findFirst()
            .getOrNull()

        return result
    }

    fun extractComponentName(input: String): String? {
        // Regex to match the text between <col=...> and </col>
        val regex = """<col=[^>]+>(.*?)</col>""".toRegex()
        val match = regex.find(input)
        return match?.groups?.get(1)?.value // Get the captured group
    }
}